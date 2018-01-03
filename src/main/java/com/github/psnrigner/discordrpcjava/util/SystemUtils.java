package com.github.psnrigner.discordrpcjava.util;

public class SystemUtils
{

    public static boolean isWindows()
    {
        return is(OS.WINDOWS);
    }

    public static boolean isMacOSX()
    {
        return is(OS.MAC_OSX);
    }

    public static boolean isUnix()
    {
        return is(OS.UNIX);
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    private static boolean is(OS os)
    {
        return OS.byOsName(System.getProperty("os.name")).equals(os);
    }

    public enum OS {
        WINDOWS(new String[]{"Windows"}),
        MAC_OSX(new String[]{"Mac"}),
        UNIX(new String[]{"Linux", "LINUX", "AIX", "HP-UX", "Trix", "OS/2", "Solaris", "SunOS"}), // feel free to add
        UNKNOWN(new String[]{""});

        private String[] osNames;

        OS(String[] osNames)
        {
            this.osNames = osNames;
        }

        public String[] getOsNames()
        {
            return osNames;
        }

        public static OS byOsName(String osName)
        {
            for(OS os: OS.values()) {
                for(String name : os.getOsNames())
                {
                    if(osName.startsWith(name))
                    {
                        return os;
                    }
                }
            }
            return UNKNOWN;
        }
    }
}
