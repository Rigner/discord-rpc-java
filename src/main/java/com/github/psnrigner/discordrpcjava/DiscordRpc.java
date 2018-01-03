package com.github.psnrigner.discordrpcjava;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.github.psnrigner.discordrpcjava.impl.Backoff;
import com.github.psnrigner.discordrpcjava.impl.RpcConnection;

import java.lang.management.ManagementFactory;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main SDK class
 */
public class DiscordRpc
{
    private final boolean disableIoThread;

    private long pid;
    private long nonce;
    private DiscordEventHandler handler;
    private RpcConnection rpcConnection;
    private final Backoff reconnectTimeMs;

    private long nextConnect;

    private String joinGameSecret;
    private String spectateGameSecret;
    private ErrorCode lastErrorCode;
    private String lastErrorMessage;
    private ErrorCode lastDisconnectErrorCode;
    private String lastDisconnectErrorMessage;

    private final AtomicBoolean wasJustConnected;
    private final AtomicBoolean wasJustDisconnected;
    private final AtomicBoolean gotErrorMessage;
    private final AtomicBoolean wasJoinGame;
    private final AtomicBoolean wasSpectateGame;

    private final Queue<byte[]> sendQueue;
    private final Queue<byte[]> presenceQueue;
    private final Queue<DiscordJoinRequest> joinAskQueue;

    private final AtomicBoolean keepRunning;
    private final Lock waitForIoMutex;
    private final Condition waitForIoActivity;
    private Thread ioThread;

    /**
     * Create a Discord SDK instance
     */
    public DiscordRpc()
    {
        this(false);
    }

    /**
     * Create a Discord DSK instance
     *
     * @param disableIoThread If SDK default I/O thread should be disabled
     */
    public DiscordRpc(boolean disableIoThread)
    {
        this.disableIoThread = disableIoThread;

        this.pid = -1;
        this.nonce = 1;
        this.handler = null;
        this.rpcConnection = null;
        this.reconnectTimeMs = new Backoff(500L, 60000L);

        this.nextConnect = System.currentTimeMillis();

        this.wasJustConnected = new AtomicBoolean(false);
        this.wasJustDisconnected = new AtomicBoolean(false);
        this.gotErrorMessage = new AtomicBoolean(false);
        this.wasJoinGame = new AtomicBoolean(false);
        this.wasSpectateGame = new AtomicBoolean(false);

        this.sendQueue = new ConcurrentLinkedQueue<>();
        this.presenceQueue = new ConcurrentLinkedQueue<>();
        this.joinAskQueue = new ConcurrentLinkedQueue<>();

        this.keepRunning = new AtomicBoolean(true);
        this.waitForIoMutex = new ReentrantLock(true);
        this.waitForIoActivity = this.waitForIoMutex.newCondition();
        this.ioThread = null;
    }

    /**
     * Initialise the connection to Discord
     *
     * @param applicationId Application ID
     * @param handler Event Handler
     * @param autoRegister If the SDK should register the application
     */
    public void init(String applicationId, DiscordEventHandler handler, boolean autoRegister)
    {
        this.init(applicationId, handler, autoRegister, null);
    }

    /**
     * Initialise the connection to Discord
     *
     * @param applicationId Application ID
     * @param handler Event Handler
     * @param autoRegister If the SDK should register the application
     * @param optionalSteamId Application Steam ID
     */
    public void init(String applicationId, DiscordEventHandler handler, boolean autoRegister, String optionalSteamId)
    {
        if (this.rpcConnection != null)
            return;

        this.pid = this.getProcessId();
        this.handler = handler;

        this.rpcConnection = RpcConnection.create(applicationId);

        if (autoRegister)
        {
            if (optionalSteamId != null && !optionalSteamId.isEmpty())
                this.registerSteamGame(applicationId, optionalSteamId);
            else
                this.register(applicationId, null);
        }

        this.rpcConnection.setConnectCallback(() ->
        {
            this.wasJustConnected.set(true);

            this.reconnectTimeMs.reset();

            if (this.handler != null)
            {
                this.registerForEvent("ACTIVITY_JOIN");
                this.registerForEvent("ACTIVITY_SPECTATE");
                this.registerForEvent("ACTIVITY_JOIN_REQUEST");
            }
        });

        this.rpcConnection.setDisconnectCallback((lastErrorCode, lastErrorMessage) ->
        {
            this.lastDisconnectErrorCode = lastErrorCode;
            this.lastDisconnectErrorMessage = lastErrorMessage;
            this.wasJustDisconnected.set(true);
            this.updateReconnectTime();
        });

        if (!this.disableIoThread)
        {
            this.keepRunning.set(true);
            this.ioThread = new Thread(this::discordRpcIo);
            this.ioThread.start();
        }
    }

    /**
     * Shutdown the connection and all I/O operations
     */
    public void shutdown()
    {
        if (this.rpcConnection == null)
            return;

        this.rpcConnection.setConnectCallback(null);
        this.rpcConnection.setDisconnectCallback(null);
        this.handler = null;

        if (!this.disableIoThread)
        {
            this.keepRunning.set(false);

            this.signalIoActivity();

            try
            {
                this.ioThread.join();
            }
            catch (Exception ignored)
            {
            }
        }

        RpcConnection.destroy(this.rpcConnection);
        this.rpcConnection = null;
    }

    /**
     * Send a presence update to Discord
     *
     * @param discordRichPresence Rich presence information
     */
    public void updatePresence(DiscordRichPresence discordRichPresence)
    {
        JsonObject jsonObject = discordRichPresence.toJson(this.pid, this.nonce++);

        this.presenceQueue.offer(jsonObject.toString().getBytes());

        this.signalIoActivity();
    }

    /**
     * Respond to a join / spectate request
     *
     * @param userId ID of user who requested to join
     * @param reply Reply, can be either YES, NO, or IGNORE
     */
    public void respond(String userId, DiscordReply reply)
    {
        if (this.rpcConnection == null || !this.rpcConnection.isOpen())
            return;

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cmd", new JsonPrimitive(reply == DiscordReply.YES ? "SEND_ACTIVITY_JOIN_INVITE" : "CLOSE_ACTIVITY_JOIN_REQUEST"));

        JsonObject args = new JsonObject();
        args.add("user_id", new JsonPrimitive(userId));
        jsonObject.add("args", args);
        jsonObject.add("nonce", new JsonPrimitive(String.valueOf(this.nonce++)));

        byte[] bytes = jsonObject.toString().getBytes();

        if (this.sendQueue.offer(bytes))
            this.signalIoActivity();
    }

    /**
     * Ask the SDK to run all callbacks
     *
     * If an event handler is specified in {@link DiscordRpc#init(String, DiscordEventHandler, boolean)}, calls all queued events
     */
    public void runCallbacks()
    {
        if (this.rpcConnection == null)
            return;

        if (this.handler != null)
        {
            boolean wasDisconnected = this.wasJustDisconnected.getAndSet(false);
            boolean isConnected = this.rpcConnection.isOpen();

            if (isConnected && wasDisconnected)
                this.handler.disconnected(this.lastErrorCode, this.lastErrorMessage);

            if (this.wasJustConnected.getAndSet(false))
                this.handler.ready();

            if (this.gotErrorMessage.getAndSet(false))
                this.handler.errored(this.lastErrorCode, this.lastErrorMessage);

            if (this.wasJoinGame.getAndSet(false))
                this.handler.joinGame(this.joinGameSecret);

            if (this.wasSpectateGame.getAndSet(false))
                this.handler.spectateGame(this.spectateGameSecret);

            DiscordJoinRequest request;
            while ((request = this.joinAskQueue.poll()) != null)
                if (this.handler != null)
                    this.handler.joinRequest(request);

            if (!isConnected && wasDisconnected)
                this.handler.disconnected(this.lastDisconnectErrorCode, this.lastDisconnectErrorMessage);
        }
    }

    /**
     * Manually register a steam game
     *
     * @param applicationId Application ID
     * @param optionalSteamId Application Steam ID
     */
    public void registerSteamGame(String applicationId, String optionalSteamId)
    {
        if (this.rpcConnection != null)
            this.rpcConnection.getBaseConnection().registerSteamGame(applicationId, optionalSteamId);
    }

    /**
     * Manually register a application
     *
     * @param applicationId Application ID
     * @param command Command to run the application
     */
    public void register(String applicationId, String command)
    {
        if (this.rpcConnection != null)
            this.rpcConnection.getBaseConnection().register(applicationId, command);
    }

    private void registerForEvent(String name)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cmd", new JsonPrimitive("SUBSCRIBE"));
        jsonObject.add("evt", new JsonPrimitive(name));
        jsonObject.add("nonce", new JsonPrimitive(String.valueOf(this.nonce++)));

        byte[] bytes = jsonObject.toString().getBytes();

        if (this.sendQueue.offer(bytes))
            this.signalIoActivity();
    }

    private void updateReconnectTime()
    {
        this.nextConnect = System.currentTimeMillis() + this.reconnectTimeMs.nextDelay();
    }

    private void discordRpcIo()
    {
        while (this.keepRunning.get())
        {
            this.updateConnection();

            this.waitForIoMutex.lock();

            try
            {
                this.waitForIoActivity.await(500, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException ignored)
            {
            }
            finally
            {
                this.waitForIoMutex.unlock();
            }
        }
    }

    private void signalIoActivity()
    {
        this.waitForIoMutex.lock();

        try
        {
            this.waitForIoActivity.signalAll();
        }
        catch (Exception ignored)
        {
        }
        finally
        {
            this.waitForIoMutex.unlock();
        }
    }

    /**
     * Updates the connection, read incoming data
     */
    public void updateConnection()
    {
        if (this.rpcConnection == null)
            return;

        if (!this.rpcConnection.isOpen())
        {
            if (System.currentTimeMillis() >= this.nextConnect)
            {
                this.updateReconnectTime();
                this.rpcConnection.open();
            }
        }
        else
        {
            while (true)
            {
                JsonObject message = new JsonObject();

                if (!this.rpcConnection.read(message, false))
                    break;

                String evtName = message.has("evt") && !message.get("evt").isJsonNull() ? message.get("evt").getAsString() : null;
                String nonce = message.has("nonce") && !message.get("nonce").isJsonNull() ? message.get("nonce").getAsString() : null;

                if (nonce != null)
                {
                    if (evtName != null && evtName.equals("ERROR"))
                    {
                        JsonObject data = message.get("data").getAsJsonObject();
                        int error = data.get("code").getAsInt();
                        this.lastErrorCode = data.has("code") ? error >= ErrorCode.values().length ? ErrorCode.UNKNOWN : ErrorCode.values()[error] : ErrorCode.SUCCESS;
                        this.lastErrorMessage = data.has("message") ? data.get("message").getAsString() : "";
                        this.gotErrorMessage.set(true);
                    }
                }
                else
                {
                    if (evtName == null)
                        continue;

                    switch (evtName)
                    {
                        case "ACTIVITY_JOIN":
                        {
                            JsonObject data = message.get("data").getAsJsonObject();
                            String secret = data.has("secret") ? data.get("secret").getAsString() : null;

                            if (secret != null)
                            {
                                this.joinGameSecret = secret;
                                this.wasJoinGame.set(true);
                            }
                            break;
                        }
                        case "ACTIVITY_SPECTATE":
                        {
                            JsonObject data = message.get("data").getAsJsonObject();
                            String secret = data.has("secret") ? data.get("secret").getAsString() : null;

                            if (secret != null)
                            {
                                this.spectateGameSecret = secret;
                                this.wasSpectateGame.set(true);
                            }
                            break;
                        }
                        case "ACTIVITY_JOIN_REQUEST":
                        {
                            JsonObject data = message.get("data").getAsJsonObject();
                            JsonObject user = data.get("user").getAsJsonObject();
                            String userId = user.has("id") ? user.get("id").getAsString() : null;
                            String username = user.has("username") ? user.get("username").getAsString() : null;
                            String discriminator = user.has("") ? user.get("discriminator").getAsString() : null;
                            String avatar = user.has("avatar") ? user.get("avatar").getAsString() : null;

                            if (userId != null && username != null)
                            {
                                DiscordJoinRequest discordJoinRequest = new DiscordJoinRequest(userId, username, discriminator, avatar == null ? "" : avatar);

                                this.joinAskQueue.offer(discordJoinRequest);
                            }

                            break;
                        }
                    }
                }
            }

            if (!this.presenceQueue.isEmpty())
            {
                byte[] bytes;

                while ((bytes = this.presenceQueue.peek()) != null)
                {
                    if (!this.rpcConnection.write(bytes))
                        break;
                    else
                        this.presenceQueue.poll();
                }
            }

            if (!this.sendQueue.isEmpty())
            {
                byte[] bytes;

                while ((bytes = this.sendQueue.poll()) != null)
                    this.rpcConnection.write(bytes);
            }
        }
    }

    private long getProcessId()
    {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf('@');

        if (index < 1)
            return -1;

        try
        {
            return Long.parseLong(jvmName.substring(0, index));
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }
}
