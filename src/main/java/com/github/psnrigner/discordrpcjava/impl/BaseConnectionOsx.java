package com.github.psnrigner.discordrpcjava.impl;

import java.io.FileWriter;

class BaseConnectionOsx extends BaseConnectionUnix
{
    private void registerCommand(String applicationId, String command)
    {
        String home = System.getenv("HOME");
        if (home == null)
            throw new RuntimeException("Unable to find user HOME directory");

        String path = home + "/Library/Application Support/discord";

        if (!this.mkdir(path))
            throw new RuntimeException("Failed to create directory '" + path + "'");

        path += "/games";

        if (!this.mkdir(path))
            throw new RuntimeException("Failed to create directory '" + path + "'");

        path += "/" + applicationId + ".json";

        try (FileWriter fileWriter = new FileWriter(path))
        {
            fileWriter.write("{\"command\": \"" + command + "\"}");
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to write fame info into '" + path + "'");
        }
    }

    private void registerUrl(String applicationId)
    {
        throw new UnsupportedOperationException("OSX URL registration is not handled yet");
        /* TODO
        char url[256];
        snprintf(url, sizeof(url), "discord-%s", applicationId);
        CFStringRef cfURL = CFStringCreateWithCString(NULL, url, kCFStringEncodingUTF8);

        NSString* myBundleId = [[NSBundle mainBundle] bundleIdentifier];
        if (!myBundleId) {
            fprintf(stderr, "No bundle id found\n");
            return;
        }

        NSURL* myURL = [[NSBundle mainBundle] bundleURL];
        if (!myURL) {
            fprintf(stderr, "No bundle url found\n");
            return;
        }

        OSStatus status = LSSetDefaultHandlerForURLScheme(cfURL, (__bridge CFStringRef)myBundleId);
        if (status != noErr) {
            fprintf(stderr, "Error in LSSetDefaultHandlerForURLScheme: %d\n", (int)status);
            return;
        }

        status = LSRegisterURL((__bridge CFURLRef)myURL, true);
        if (status != noErr) {
            fprintf(stderr, "Error in LSRegisterURL: %d\n", (int)status);
        }
         */
    }

    @Override
    public void register(String applicationId, String command)
    {
        try
        {
            if (command != null)
                this.registerCommand(applicationId, command);
            else
                this.registerUrl(applicationId);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to register " + (command == null ? "url" : "command"), ex);
        }
    }

    @Override
    public void registerSteamGame(String applicationId, String steamId)
    {
        this.register(applicationId, "steam://rungameid/" + steamId);
    }
}
