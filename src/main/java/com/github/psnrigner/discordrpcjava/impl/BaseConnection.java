package com.github.psnrigner.discordrpcjava.impl;


import com.github.psnrigner.discordrpcjava.util.SystemUtils;

/**
 * Base connection class
 *
 * Implementation is OS dependent
 */
public abstract class BaseConnection
{
    BaseConnection()
    {
    }

    static BaseConnection create()
    {
        switch (SystemUtils.getOs()) {
            case WINDOWS:
                return new BaseConnectionWindows();
            case MAC_OSX:
                return new BaseConnectionOsx();
            case UNIX:
                return new BaseConnectionUnix();
            case UNKNOWN:
                break; // throw error below
        }
        throw new IllegalStateException("OS is not supported: " + SystemUtils.getOsName());
    }

    static void destroy(BaseConnection baseConnection)
    {
        baseConnection.close();
    }

    abstract boolean isOpen();

    abstract boolean open();

    abstract boolean close();

    abstract boolean write(byte[] bytes, int length);  // FIXME Lock over calls of that methods to unsure 2 threads are not writing at the same time

    abstract boolean read(byte[] bytes, int length, boolean wait);

    /**
     * Register an application
     *
     * @param applicationId Application ID
     * @param command       Command to run the application
     */
    public abstract void register(String applicationId, String command);

    /**
     * Register a Steam application
     *
     * @param applicationId Application ID
     * @param steamId       Application Steam ID
     */
    public abstract void registerSteamGame(String applicationId, String steamId);
}
