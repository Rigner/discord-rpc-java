package com.github.psnrigner.discordrpcjava;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Rich presence information
 */
public class DiscordRichPresence
{
    private String state;
    private String details;
    private long startTimestamp;
    private long endTimestamp;
    private String largeImageKey;
    private String largeImageText;
    private String smallImageKey;
    private String smallImageText;
    private String partyId;
    private int partySize;
    private int partyMax;
    private String matchSecret;
    private String joinSecret;
    private String spectateSecret;
    private boolean instance;

    public DiscordRichPresence()
    {
    }

    private DiscordRichPresence(String state, String details, long startTimestamp, long endTimestamp,
                                String largeImageKey, String largeImageText, String smallImageKey,
                                String smallImageText, String partyId, int partySize, int partyMax, String matchSecret,
                                String joinSecret, String spectateSecret, boolean instance)
    {
        this.state = state;
        this.details = details;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.largeImageKey = largeImageKey;
        this.largeImageText = largeImageText;
        this.smallImageKey = smallImageKey;
        this.smallImageText = smallImageText;
        this.partyId = partyId;
        this.partySize = partySize;
        this.partyMax = partyMax;
        this.matchSecret = matchSecret;
        this.joinSecret = joinSecret;
        this.spectateSecret = spectateSecret;
        this.instance = instance;
    }

    /**
     * Set state
     *
     * @param state New state
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * Get current state
     *
     * @return Current state
     */
    public String getState()
    {
        return this.state == null ? "" : this.state;
    }

    /**
     * Set details
     *
     * @param details New details
     */
    public void setDetails(String details)
    {
        this.details = details;
    }

    /**
     * Get current details
     *
     * @return Current details
     */
    public String getDetails()
    {
        return this.details == null ? "" : this.details;
    }

    /**
     * Set start timestamp (in millis)
     *
     * @param startTimestamp New start timestamp
     */
    public void setStartTimestamp(long startTimestamp)
    {
        this.startTimestamp = startTimestamp;
    }

    /**
     * Get current start timestamp (in millis)
     *
     * @return Current start timestamp
     */
    public long getStartTimestamp()
    {
        return this.startTimestamp;
    }

    /**
     * Set end timestamp (in millis)
     *
     * @param endTimestamp New end timestamp
     */
    public void setEndTimestamp(long endTimestamp)
    {
        this.endTimestamp = endTimestamp;
    }

    /**
     * Get current end timestamp (in millis)
     *
     * @return Current end timestamp
     */
    public long getEndTimestamp()
    {
        return this.endTimestamp;
    }

    /**
     * Set large image key
     *
     * @param largeImageKey New large image key
     */
    public void setLargeImageKey(String largeImageKey)
    {
        this.largeImageKey = largeImageKey;
    }

    /**
     * Get current large image key
     *
     * @return Current large image key
     */
    public String getLargeImageKey()
    {
        return this.largeImageKey;
    }

    /**
     * Set large image text
     *
     * @param largeImageText New large image text
     */
    public void setLargeImageText(String largeImageText)
    {
        this.largeImageText = largeImageText;
    }

    /**
     * Get current large image text
     *
     * @return Current large image text
     */
    public String getLargeImageText()
    {
        return this.largeImageText;
    }

    /**
     * Set small image key
     *
     * @param smallImageKey New small image key
     */
    public void setSmallImageKey(String smallImageKey)
    {
        this.smallImageKey = smallImageKey;
    }

    /**
     * Get current small image key
     *
     * @return Current small image key
     */
    public String getSmallImageKey()
    {
        return this.smallImageKey;
    }

    /**
     * Set small image text
     *
     * @param smallImageText New small image text
     */
    public void setSmallImageText(String smallImageText)
    {
        this.smallImageText = smallImageText;
    }

    /**
     * Get current small image text
     *
     * @return Current small image text
     */
    public String getSmallImageText()
    {
        return this.smallImageText;
    }

    /**
     * Set party id
     *
     * @param partyId New party id
     */
    public void setPartyId(String partyId)
    {
        this.partyId = partyId;
    }

    /**
     * Get current party id
     *
     * @return Current party id
     */
    public String getPartyId()
    {
        return this.partyId;
    }

    /**
     * Set party size
     *
     * @param partySize New party size
     */
    public void setPartySize(int partySize)
    {
        this.partySize = partySize;
    }

    /**
     * Get current party size
     *
     * @return Current party size
     */
    public int getPartySize()
    {
        return this.partySize;
    }

    /**
     * Set party max size
     *
     * @param partyMax New party max size
     */
    public void setPartyMax(int partyMax)
    {
        this.partyMax = partyMax;
    }

    /**
     * Get current party max size
     *
     * @return Current party max size
     */
    public int getPartyMax()
    {
        return this.partyMax;
    }

    /**
     * Set match secret
     *
     * @param matchSecret New match secret
     */
    public void setMatchSecret(String matchSecret)
    {
        this.matchSecret = matchSecret;
    }

    /**
     * Get current match secret
     *
     * @return Current match secret
     */
    public String getMatchSecret()
    {
        return this.matchSecret;
    }

    /**
     * Set join secret
     *
     * @param joinSecret Current join secret
     */
    public void setJoinSecret(String joinSecret)
    {
        this.joinSecret = joinSecret;
    }

    /**
     * Get current join secret
     *
     * @return Current join secret
     */
    public String getJoinSecret()
    {
        return this.joinSecret;
    }

    /**
     * Set spectate secret
     *
     * @param spectateSecret New spectate secret
     */
    public void setSpectateSecret(String spectateSecret)
    {
        this.spectateSecret = spectateSecret;
    }

    /**
     * Get current spectate secret
     *
     * @return Current spectate secret
     */
    public String getSpectateSecret()
    {
        return this.spectateSecret;
    }

    /**
     * Set if the game is an instance
     *
     * @param instance {@code true} if an instance
     */
    public void setInstance(boolean instance)
    {
        this.instance = instance;
    }

    /**
     * Get if the gme is an instance
     *
     * @return {@code true} if an instance
     */
    public boolean isInstance()
    {
        return this.instance;
    }

    JsonObject toJson(long pid, long nonce)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("nonce", new JsonPrimitive(String.valueOf(nonce)));
        jsonObject.add("cmd", new JsonPrimitive("SET_ACTIVITY"));

        JsonObject args = new JsonObject();
        args.add("pid", new JsonPrimitive(pid));

        JsonObject activity = new JsonObject();
        activity.add("state", new JsonPrimitive(this.getState()));
        activity.add("details", new JsonPrimitive(this.getDetails()));

        if (this.getStartTimestamp() != 0 || this.getEndTimestamp() != 0)
        {
            JsonObject timestamps = new JsonObject();

            if (this.getStartTimestamp() != 0)
                timestamps.add("start", new JsonPrimitive(this.getStartTimestamp()));

            if (this.getEndTimestamp() != 0)
                timestamps.add("end", new JsonPrimitive(this.getEndTimestamp()));

            activity.add("timestamps", timestamps);
        }

        if ((this.getLargeImageKey() != null && !this.getLargeImageKey().isEmpty())
                || (this.getLargeImageText() != null && !this.getLargeImageText().isEmpty())
                || (this.getSmallImageKey() != null && !this.getSmallImageKey().isEmpty())
                || (this.getSmallImageText() != null && !this.getSmallImageText().isEmpty()))
        {
            JsonObject assets = new JsonObject();

            if (this.getLargeImageKey() != null && !this.getLargeImageKey().isEmpty())
                assets.add("large_image", new JsonPrimitive(this.getLargeImageKey()));

            if (this.getLargeImageText() != null && !this.getLargeImageText().isEmpty())
                assets.add("large_text", new JsonPrimitive(this.getLargeImageText()));

            if (this.getSmallImageKey() != null && !this.getSmallImageKey().isEmpty())
                assets.add("small_image", new JsonPrimitive(this.getSmallImageKey()));

            if (this.getSmallImageText() != null && !this.getSmallImageText().isEmpty())
                assets.add("small_text", new JsonPrimitive(this.getSmallImageText()));

            activity.add("assets", assets);
        }

        if ((this.getPartyId() != null && !this.getPartyId().isEmpty())
                || this.getPartySize() != 0 || this.getPartyMax() != 0)
        {
            JsonObject party = new JsonObject();

            if (this.getPartyId() != null && !this.getPartyId().isEmpty())
                party.add("id", new JsonPrimitive(this.getPartyId()));

            if (this.getPartySize() != 0)
            {
                JsonArray size = new JsonArray();
                size.add(this.getPartySize());

                if (this.getPartyMax() > 0)
                    size.add(this.getPartyMax());

                party.add("size", size);
            }

            activity.add("party", party);
        }

        if ((this.getMatchSecret() != null && !this.getMatchSecret().isEmpty())
                || (this.getJoinSecret() != null && !this.getJoinSecret().isEmpty())
                || (this.getSpectateSecret() != null && !this.getSpectateSecret().isEmpty()))
        {
            JsonObject secrets = new JsonObject();

            if (this.getMatchSecret() != null && !this.getMatchSecret().isEmpty())
                secrets.add("match", new JsonPrimitive(this.getMatchSecret()));

            if (this.getJoinSecret() != null && !this.getJoinSecret().isEmpty())
                secrets.add("join", new JsonPrimitive(this.getJoinSecret()));

            if (this.getSpectateSecret() != null && !this.getSpectateSecret().isEmpty())
                secrets.add("spectate", new JsonPrimitive(this.getSpectateSecret()));

            activity.add("secrets", secrets);
        }

        activity.add("instance", new JsonPrimitive(this.isInstance()));

        args.add("activity", activity);

        jsonObject.add("args", args);

        return jsonObject;
    }

    /**
     * Create a new rich presence builder
     *
     * @return New Builder instance
     */
    public static DiscordRichPresenceBuilder builder()
    {
        return new DiscordRichPresenceBuilder();
    }

    /**
     * Rich Presence Builder class
     */
    public static class DiscordRichPresenceBuilder
    {
        private String state;
        private String details;
        private long startTimestamp;
        private long endTimestamp;
        private String largeImageKey;
        private String largeImageText;
        private String smallImageKey;
        private String smallImageText;
        private String partyId;
        private int partySize;
        private int partyMax;
        private String matchSecret;
        private String joinSecret;
        private String spectateSecret;
        private boolean instance;

        /**
         * Build a new rich presence
         *
         * @return A new rich presence instance
         */
        public DiscordRichPresence build()
        {
            return new DiscordRichPresence(this.state, this.details, this.startTimestamp, this.endTimestamp, this.largeImageKey, this.largeImageText,
                    this.smallImageKey, this.smallImageText, this.partyId, this.partySize, this.partyMax,
                    this.matchSecret, this.joinSecret, this.spectateSecret, this.instance);
        }

        /**
         * Set state
         *
         * @param state New state
         * @return This
         */
        public DiscordRichPresenceBuilder setState(String state)
        {
            this.state = state;
            return this;
        }

        /**
         * Set details
         *
         * @param details New details
         * @return This
         */
        public DiscordRichPresenceBuilder setDetails(String details)
        {
            this.details = details;
            return this;
        }

        /**
         * Set start timestamp (in millis)
         *
         * @param startTimestamp New start timestamp
         * @return This
         */
        public DiscordRichPresenceBuilder setStartTimestamp(long startTimestamp)
        {
            this.startTimestamp = startTimestamp;
            return this;
        }

        /**
         * Set end timestamp (in millis)
         *
         * @param endTimestamp New end timestamp
         * @return This
         */
        public DiscordRichPresenceBuilder setEndTimestamp(long endTimestamp)
        {
            this.endTimestamp = endTimestamp;
            return this;
        }

        /**
         * Set large image key
         *
         * @param largeImageKey New large image key
         * @return This
         */
        public DiscordRichPresenceBuilder setLargeImageKey(String largeImageKey)
        {
            this.largeImageKey = largeImageKey;
            return this;
        }

        /**
         * Set large image text
         *
         * @param largeImageText New large image text
         * @return This
         */
        public DiscordRichPresenceBuilder setLargeImageText(String largeImageText)
        {
            this.largeImageText = largeImageText;
            return this;
        }

        /**
         * Set small image key
         *
         * @param smallImageKey New small image key
         * @return This
         */
        public DiscordRichPresenceBuilder setSmallImageKey(String smallImageKey)
        {
            this.smallImageKey = smallImageKey;
            return this;
        }

        /**
         * Set small image text
         *
         * @param smallImageText New small image text
         * @return This
         */
        public DiscordRichPresenceBuilder setSmallImageText(String smallImageText)
        {
            this.smallImageText = smallImageText;
            return this;
        }

        /**
         * Set party id
         *
         * @param partyId New party id
         * @return This
         */
        public DiscordRichPresenceBuilder setPartyId(String partyId)
        {
            this.partyId = partyId;
            return this;
        }

        /**
         * Set party size
         *
         * @param partySize New party size
         * @return This
         */
        public DiscordRichPresenceBuilder setPartySize(int partySize)
        {
            this.partySize = partySize;
            return this;
        }

        /**
         * Set party max size
         *
         * @param partyMax New party max size
         * @return This
         */
        public DiscordRichPresenceBuilder setPartyMax(int partyMax)
        {
            this.partyMax = partyMax;
            return this;
        }

        /**
         * Set match secret
         *
         * @param matchSecret New match secret
         * @return This
         */
        public DiscordRichPresenceBuilder setMatchSecret(String matchSecret)
        {
            this.matchSecret = matchSecret;
            return this;
        }

        /**
         * Set join secret
         *
         * @param joinSecret New join secret
         * @return This
         */
        public DiscordRichPresenceBuilder setJoinSecret(String joinSecret)
        {
            this.joinSecret = joinSecret;
            return this;
        }

        /**
         * Set spectate secret
         *
         * @param spectateSecret New spectate secret
         * @return This
         */
        public DiscordRichPresenceBuilder setSpectateSecret(String spectateSecret)
        {
            this.spectateSecret = spectateSecret;
            return this;
        }

        /**
         * Set if the game is an instance
         *
         * @param instance {@code true} if an instance
         * @return This
         */
        public DiscordRichPresenceBuilder setInstance(boolean instance)
        {
            this.instance = instance;
            return this;
        }
    }
}
