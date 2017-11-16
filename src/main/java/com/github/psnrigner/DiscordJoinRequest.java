package com.github.psnrigner;

/**
 * Discord join request
 */
public class DiscordJoinRequest
{
    private final String userId;
    private final String username;
    private final String avatar;

    DiscordJoinRequest(String userId, String username, String avatar)
    {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * Get the id of the user requesting to join
     *
     * @return User id
     */
    public String getUserId()
    {
        return this.userId;
    }

    /**
     * Get the name of the user requesting to join
     *
     * @return Username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * Get the avatar of the user requesting to join
     *
     * @return Avatar
     */
    public String getAvatar()
    {
        return this.avatar;
    }
}
