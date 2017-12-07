package com.github.psnrigner.discordrpcjava.impl;

import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

class BaseConnectionUnix extends BaseConnection
{
    private UnixSocketChannel unixSocket;
    private boolean opened;

    BaseConnectionUnix()
    {
        this.unixSocket = null;
        this.opened = false;
    }

    private String getTempPath()
    {
        String temp = System.getenv("XDG_RUNTIME_DIR");
        temp = temp != null ? temp : System.getenv("TMPDIR");
        temp = temp != null ? temp : System.getenv("TMP");
        temp = temp != null ? temp : System.getenv("TEMP");
        temp = temp != null ? temp : "/tmp";
        return temp;
    }

    boolean mkdir(String path)
    {
        File file = new File(path);

        return file.exists() && file.isDirectory() || file.mkdir();
    }

    @Override
    boolean isOpen()
    {
        return this.opened;
    }

    @Override
    boolean open()
    {
        String pipeName = this.getTempPath() + "/discord-ipc-";

        if (this.isOpen())
            throw new IllegalStateException("Connection is already opened");

        int pipeDigit = 0;
        while (pipeDigit < 10)
        {
            try
            {
                UnixSocketAddress address = new UnixSocketAddress(pipeName + pipeDigit);
                this.unixSocket = UnixSocketChannel.open(address);
                this.opened = true;

                return true;
            }
            catch (Exception ex)
            {
                ++pipeDigit;
            }
        }

        return false;
    }

    @Override
    boolean close()
    {
        if (!this.isOpen())
            return true;

        try
        {
            this.unixSocket.close();
        }
        catch (IOException ignored)
        {
        }

        this.unixSocket = null;
        this.opened = false;

        return true;
    }

    @Override
    boolean write(byte[] bytes, int length)
    {
        if (bytes == null || length == 0)
            return bytes != null;

        if (!this.isOpen())
            return false;

        try
        {
            ByteBuffer byteBuffer = ByteBuffer.allocate(length);
            byteBuffer.put(bytes, 0, length);

            this.unixSocket.write(byteBuffer);
            return true;
        }
        catch (IOException ignored)
        {
            return false;
        }
    }

    @Override
    boolean read(byte[] bytes, int length, boolean wait)
    {
        if (bytes == null || bytes.length == 0)
            return bytes != null;

        if (!this.isOpen())
            return false;

        try
        {
            if (!wait)
            {
                long available = this.unixSocket.socket().getInputStream().available();

                if (available < length)
                    return false;
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(length);

            int read = this.unixSocket.read(byteBuffer);

            if (read != length)
                throw new IOException();

            byteBuffer.get(bytes, 0, length);

            return true;
        }
        catch (IOException ignored)
        {
            this.close();
            return false;
        }
    }

    @Override
    public void register(String applicationId, String command)
    {
        String home = System.getenv("HOME");

        if (home == null)
            throw new RuntimeException("Unable to find user HOME directory");

        if (command == null)
        {
            try
            {
                command = Files.readSymbolicLink(Paths.get("/proc/self/exe")).toString();
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Unable to get current exe path from /proc/self/exe", ex);
            }
        }

        String desktopFile =
                "[Desktop Entry]\n" +
                "Name=Game " + applicationId + "\n" +
                "Exec=" + command + " %%u\n" +
                "Type=Application\n" +
                "NoDisplay=true\n" +
                "Categories=Discord;Games;\n" +
                "MimeType=x-scheme-handler/discord-" + applicationId + ";\n";

        String desktopFileName = "/discord-" + applicationId + ".desktop";
        String desktopFilePath = home + "/.local";

        if (this.mkdir(desktopFilePath))
            throw new RuntimeException("Failed to create directory '" + desktopFilePath + "'");

        desktopFilePath += "/share";

        if (this.mkdir(desktopFilePath))
            throw new RuntimeException("Failed to create directory '" + desktopFilePath + "'");

        desktopFilePath += "/applications";

        if (this.mkdir(desktopFilePath))
            throw new RuntimeException("Failed to create directory '" + desktopFilePath + "'");

        desktopFilePath += desktopFileName;

        try (FileWriter fileWriter = new FileWriter(desktopFilePath))
        {
            fileWriter.write(desktopFile);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to write desktop info into '" + desktopFilePath + "'");
        }

        String xdgMimeCommand = "xdg-mime default discord-" + applicationId + ".desktop x-scheme-handler/discord-" + applicationId;

        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(xdgMimeCommand.split(" "));
            processBuilder.environment();
            int result = processBuilder.start().waitFor();
            if (result < 0)
                throw new Exception("xdg-mime returned " + result);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to register mime handler", ex);
        }
    }

    @Override
    public void registerSteamGame(String applicationId, String steamId)
    {
        this.register(applicationId, "xdg-open steam://rungameid/" + steamId);
    }
}
