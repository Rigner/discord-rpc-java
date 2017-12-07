package com.github.psnrigner.discordrpcjava.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

class WinRegistry
{
    static final int HKEY_CURRENT_USER = 0x80000001;
    private static final int HKEY_LOCAL_MACHINE = 0x80000002;
    private static final int REG_SUCCESS = 0;

    private static final int KEY_ALL_ACCESS = 0xf003f;
    private static final int KEY_READ = 0x20019;
    private static final Preferences userRoot = Preferences.userRoot();
    private static final Preferences systemRoot = Preferences.systemRoot();
    private static final Class<? extends Preferences> userClass = WinRegistry.userRoot.getClass();
    private static final Method regOpenKey;
    private static final Method regCloseKey;
    private static final Method regQueryValueEx;
    private static final Method regEnumValue;
    private static final Method regQueryInfoKey;
    private static final Method regEnumKeyEx;
    private static final Method regCreateKeyEx;
    private static final Method regSetValueEx;
    private static final Method regDeleteKey;
    private static final Method regDeleteValue;

    static
    {
        try
        {
            regOpenKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegOpenKey", int.class, byte[].class, int.class);
            regOpenKey.setAccessible(true);
            regCloseKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegCloseKey", int.class);
            regCloseKey.setAccessible(true);
            regQueryValueEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegQueryValueEx", int.class, byte[].class);
            regQueryValueEx.setAccessible(true);
            regEnumValue = WinRegistry.userClass.getDeclaredMethod("WindowsRegEnumValue", int.class, int.class, int.class);
            regEnumValue.setAccessible(true);
            regQueryInfoKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", int.class);
            regQueryInfoKey.setAccessible(true);
            regEnumKeyEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegEnumKeyEx", int.class, int.class, int.class);
            regEnumKeyEx.setAccessible(true);
            regCreateKeyEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegCreateKeyEx", int.class, byte[].class);
            regCreateKeyEx.setAccessible(true);
            regSetValueEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegSetValueEx", int.class, byte[].class, byte[].class);
            regSetValueEx.setAccessible(true);
            regDeleteValue = WinRegistry.userClass.getDeclaredMethod("WindowsRegDeleteValue", int.class, byte[].class);
            regDeleteValue.setAccessible(true);
            regDeleteKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegDeleteKey", int.class, byte[].class);
            regDeleteKey.setAccessible(true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private WinRegistry()
    {
    }

    static String readString(int hkey, String key, String valueName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        if (hkey == WinRegistry.HKEY_LOCAL_MACHINE)
            return WinRegistry.readString(WinRegistry.systemRoot, hkey, key, valueName);
        else if (hkey == WinRegistry.HKEY_CURRENT_USER)
            return WinRegistry.readString(WinRegistry.userRoot, hkey, key, valueName);
        else
            throw new IllegalArgumentException("hkey=" + hkey);
    }

    static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        int[] ret;

        if (hkey == WinRegistry.HKEY_LOCAL_MACHINE)
        {
            ret = WinRegistry.createKey(WinRegistry.systemRoot, hkey, key);
            WinRegistry.regCloseKey.invoke(WinRegistry.systemRoot, ret[0]);
        }
        else if (hkey == WinRegistry.HKEY_CURRENT_USER)
        {
            ret = WinRegistry.createKey(WinRegistry.userRoot, hkey, key);
            WinRegistry.regCloseKey.invoke(WinRegistry.userRoot, ret[0]);
        }
        else
            throw new IllegalArgumentException("hkey=" + hkey);

        if (ret[1] != REG_SUCCESS)
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
    }

    static void writeStringValue(int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        if (hkey == WinRegistry.HKEY_LOCAL_MACHINE)
            WinRegistry.writeStringValue(WinRegistry.systemRoot, hkey, key, valueName, value);
        else if (hkey == WinRegistry.HKEY_CURRENT_USER)
            WinRegistry.writeStringValue(WinRegistry.userRoot, hkey, key, valueName, value);
        else
            throw new IllegalArgumentException("hkey=" + hkey);
    }


    // =====================

    private static String readString(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        int[] handles = (int[]) WinRegistry.regOpenKey.invoke(root, hkey, toCstr(key), WinRegistry.KEY_READ);
        if (handles[1] != WinRegistry.REG_SUCCESS)
            return null;

        byte[] valb = (byte[]) WinRegistry.regQueryValueEx.invoke(root, handles[0], toCstr(value));
        WinRegistry.regCloseKey.invoke(root, handles[0]);
        return valb != null ? new String(valb).trim() : null;
    }

    private static int[] createKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        return (int[]) WinRegistry.regCreateKeyEx.invoke(root, hkey, toCstr(key));
    }

    private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        int[] handles = (int[]) WinRegistry.regOpenKey.invoke(root, hkey, toCstr(key), WinRegistry.KEY_ALL_ACCESS);

        WinRegistry.regSetValueEx.invoke(root, handles[0], WinRegistry.toCstr(valueName), WinRegistry.toCstr(value));
        WinRegistry.regCloseKey.invoke(root, handles[0]);
    }

    // utility
    private static byte[] toCstr(String str)
    {
        byte[] result = new byte[str.length() + 1];

        for (int i = 0; i < str.length(); i++)
            result[i] = (byte) str.charAt(i);

        result[str.length()] = 0;
        return result;
    }
}