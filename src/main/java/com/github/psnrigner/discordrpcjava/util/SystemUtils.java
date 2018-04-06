package com.github.psnrigner.discordrpcjava.util;

public class SystemUtils
{
    public static OS getOs() {
        return OS.byOsName(getOsName());
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public enum OS {
        WINDOWS(new String[]{"Windows"}),
        MAC_OSX(new String[]{"Mac"}),
        UNIX(new String[]{"Linux", "LINUX", "AIX", "HP-UX", "Trix", "OS/2", "Solaris", "SunOS"}), // feel free to add
        UNKNOWN;

        private String[] osNames;

        OS()
        {
        }

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
