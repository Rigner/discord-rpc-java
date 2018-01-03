package com.github.psnrigner.discordrpcjava.examples;

import com.github.psnrigner.discordrpcjava.DiscordEventHandler;
import com.github.psnrigner.discordrpcjava.DiscordJoinRequest;
import com.github.psnrigner.discordrpcjava.DiscordRichPresence;
import com.github.psnrigner.discordrpcjava.DiscordRpc;
import com.github.psnrigner.discordrpcjava.ErrorCode;

import javax.swing.*;

public class InClass
{
    public static void main(String[] args)
    {
        if (args.length == 0)
            System.err.println("You need to provide a application id");
        else
        {
            JFrame frame = new JFrame("Discord-RPC-Java");

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.pack();

            frame.setVisible(true);
            InClass.run(args[0]);

            frame.dispose();

            System.out.println("Exiting");
        }
    }

    private static void run(String applicationId)
    {
        DiscordRpc discordRpc = new DiscordRpc();

        DiscordEventHandler discordEventHandler = new DiscordEventHandler()
        {
            @Override
            public void ready()
            {
                System.err.println("CLASS READY");
            }

            @Override
            public void disconnected(ErrorCode errorCode, String message)
            {
                System.err.println("CLASS DISCONNECTED : " + errorCode + " " + message);
            }

            @Override
            public void errored(ErrorCode errorCode, String message)
            {
                System.err.println("CLASS ERRORED : " + errorCode + " " + message);
            }

            @Override
            public void joinGame(String joinSecret)
            {
                System.err.println("CLASS JOIN GAME : " + joinSecret);
            }

            @Override
            public void spectateGame(String spectateSecret)
            {
                System.err.println("CLASS SPECTATE GAME : " + spectateSecret);
            }

            @Override
            public void joinRequest(DiscordJoinRequest joinRequest)
            {
                System.err.println("CLASS JOIN REQUEST : " + joinRequest);
            }
        };

        try
        {
            discordRpc.init(applicationId, discordEventHandler, true, null);

            Thread.sleep(5000L);
            discordRpc.runCallbacks();

            long start = System.currentTimeMillis() / 1000L;
            long end = System.currentTimeMillis() / 1000L + 14400L;

            for (int i = 0; i < 10000; ++i)
            {
                DiscordRichPresence discordRichPresence = new DiscordRichPresence();
                discordRichPresence.setState("In Class");
                discordRichPresence.setDetails("Database Management");
                discordRichPresence.setStartTimestamp(start);
                discordRichPresence.setEndTimestamp(end);
                discordRichPresence.setLargeImageKey("icon-large");
                discordRichPresence.setSmallImageKey("icon-small");
                discordRichPresence.setPartyId("ALONE");
                discordRichPresence.setPartySize(40);
                discordRichPresence.setPartyMax(70);
                discordRichPresence.setMatchSecret("hello");
                discordRichPresence.setJoinSecret("join");
                discordRichPresence.setSpectateSecret("look");
                discordRichPresence.setInstance(false);

                discordRpc.updatePresence(discordRichPresence);

                Thread.sleep(5000L);

                discordRpc.runCallbacks();

                Thread.sleep(5000L);
            }
        }
        catch (InterruptedException ignored)
        {
        }
        finally
        {
            discordRpc.shutdown();
        }
    }
}
