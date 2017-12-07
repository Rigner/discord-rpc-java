package com.github.psnrigner.discordrpcjava.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class BaseConnectionWindows extends BaseConnection
{
    private RandomAccessFile pipe;
    private boolean opened;

    BaseConnectionWindows()
    {
        this.pipe = null;
        this.opened = false;
    }

    @Override
    boolean isOpen()
    {
        return this.opened;
    }

    @Override
    boolean open()
    {
        String pipeName = "\\\\?\\pipe\\discord-ipc-";

        if (this.isOpen())
            throw new IllegalStateException("Connection is already opened");

        int pipeDigit = 0;
        while (pipeDigit < 10)
        {
            try
            {
                this.pipe = new RandomAccessFile(pipeName + pipeDigit, "rw");
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
            this.pipe.close();
        }
        catch (IOException ignored)
        {
        }

        this.pipe = null;
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
            this.pipe.write(bytes, 0, length);
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
                long available = this.pipe.length() - this.pipe.getFilePointer();

                if (available < length)
                    return false;
            }

            int read = this.pipe.read(bytes, 0, length);

            if (read != length)
                throw new IOException();

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
        String javaLibraryPath = System.getProperty("java.home");
        File javaExeFile = new File(javaLibraryPath.split(";")[0] + "/bin/java.exe");
        File javawExeFile = new File(javaLibraryPath.split(";")[0] + "/bin/javaw.exe");
        String javaExePath = javaExeFile.exists() ? javaExeFile.getAbsolutePath() : javawExeFile.exists() ? javawExeFile.getAbsolutePath() : null;

        if (javaExePath == null)
            throw new RuntimeException("Unable to find java path");

        String openCommand;

        if (command != null)
            openCommand = command;
        else
            openCommand = javaExePath;

        String protocolName = "discord-" + applicationId;
        String protocolDescription = "URL:Run game " + applicationId + " protocol";
        String keyName = "Software\\Classes\\" + protocolName;
        String iconKeyName = keyName + "\\DefaultIcon";
        String commandKeyName = keyName + "\\DefaultIcon";

        try
        {
            WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, keyName);
            WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, keyName, "", protocolDescription);
            WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, keyName, "URL Protocol", "\0");

            WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, iconKeyName);
            WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, iconKeyName, "", javaExePath);

            WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, commandKeyName);
            WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, commandKeyName, "", openCommand);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unable to modify Discord registry keys", ex);
        }
    }

    @Override
    public void registerSteamGame(String applicationId, String steamId)
    {
        try
        {
            String steamPath = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "Software\\\\Valve\\\\Steam", "SteamExe");
            if (steamPath == null)
                throw new RuntimeException("Steam exe path not found");

            steamPath = steamPath.replaceAll("/", "\\");

            String command = "\"" + steamPath + "\" steam://rungameid/" + steamId;

            this.register(applicationId, command);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unable to register Steam game", ex);
        }
    }
}
