package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.ddiscordbot.DiscordConnection;
import com.denizenscript.ddiscordbot.DenizenDiscordBot;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagRunnable;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.*;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.discordjson.json.ChannelModifyRequest;

public class DiscordChannelTag implements ObjectTag, Adjustable {

    // <--[language]
    // @name DiscordChannelTag Objects
    // @group Object System
    // @plugin dDiscordBot
    // @description
    // A DiscordChannelTag is an object that represents a channel (text or voice) on Discord, either as a generic reference,
    // or as a bot-specific reference (the relevant guild is inherently linked, and does not need to be specified).
    //
    // These use the object notation "discordchannel@".
    // The identity format for Discord channels is the bot ID (optional), followed by the channel ID (required).
    // For example: 1234
    // Or: mybot,1234
    //
    // -->

    @Fetchable("discordchannel")
    public static DiscordChannelTag valueOf(String string, TagContext context) {
        if (string.startsWith("discordchannel@")) {
            string = string.substring("discordchannel@".length());
        }
        if (string.contains("@")) {
            return null;
        }
        int comma = string.indexOf(',');
        String bot = null;
        if (comma > 0) {
            bot = CoreUtilities.toLowerCase(string.substring(0, comma));
            string = string.substring(comma + 1);
        }
        if (!ArgumentHelper.matchesInteger(string)) {
            if (context == null || context.debug) {
                Debug.echoError("DiscordChannelTag input is not a number.");
            }
            return null;
        }
        long chanID = Long.parseLong(string);
        if (chanID == 0) {
            return null;
        }
        return new DiscordChannelTag(bot, chanID);
    }

    public static boolean matches(String arg) {
        if (arg.startsWith("discordchannel@")) {
            return true;
        }
        if (arg.contains("@")) {
            return false;
        }
        int comma = arg.indexOf(',');
        if (comma == -1) {
            return ArgumentHelper.matchesInteger(arg);
        }
        if (comma == arg.length() - 1) {
            return false;
        }
        return ArgumentHelper.matchesInteger(arg.substring(comma + 1));
    }

    public DiscordChannelTag(String bot, long channelId) {
        this.bot = bot;
        this.channel_id = channelId;
    }

    public DiscordChannelTag(String bot, Channel channel) {
        this.bot = bot;
        this.channel = channel;
        channel_id = channel.getId().asLong();
    }

    public DiscordConnection getBot() {
        return DenizenDiscordBot.instance.connections.get(bot);
    }

    public DiscordConnection.ChannelCache getCacheChannel() {
        for (DiscordConnection.GuildCache cache : getBot().guildsCached) {
            for (DiscordConnection.ChannelCache chan : cache.channels) {
                if (chan.id == channel_id) {
                    return chan;
                }
            }
        }
        return null;
    }

    public Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        channel = getBot().client.getChannelById(Snowflake.of(channel_id)).block();
        return channel;
    }

    public Channel channel;

    public String bot;

    public long channel_id;

    public static void registerTags() {

        // <--[tag]
        // @attribute <DiscordChannelTag.name>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the name of the channel.
        // -->
        registerTag("name", (attribute, object) -> {
            DiscordConnection.ChannelCache cache = object.getCacheChannel();
            if (cache != null) {
                return new ElementTag(cache.name);
            }
            Channel chan = object.getChannel();
            String name;
            if (chan instanceof GuildChannel) {
                name = ((GuildChannel) chan).getName();
            }
            else if (chan instanceof PrivateChannel) {
                name = "private";
            }
            else {
                name = "unknown";
            }
            return new ElementTag(name);

        });

        // <--[tag]
        // @attribute <DiscordChannelTag.channel_type>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the type of the channel.
        // Will be any of: GUILD_TEXT, DM, GUILD_VOICE, GROUP_DM, GUILD_CATEGORY
        // -->
        registerTag("channel_type", (attribute, object) -> {
            DiscordConnection.ChannelCache cache = object.getCacheChannel();
            if (cache != null) {
                return new ElementTag(cache.type.name());
            }
            return new ElementTag(object.getChannel().getType().name());

        }, "type");

        // <--[tag]
        // @attribute <DiscordChannelTag.id>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @description
        // Returns the ID number of the channel.
        // -->
        registerTag("id", (attribute, object) -> {
            return new ElementTag(object.channel_id);

        });

        // <--[tag]
        // @attribute <DiscordChannelTag.mention>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the raw mention string for the channel.
        // -->
        registerTag("mention", (attribute, object) -> {
            return new ElementTag("<#" + Snowflake.of(object.getCacheChannel().id).asString() + ">");

        });

        // <--[tag]
        // @attribute <DiscordChannelTag.group>
        // @returns DiscordGroupTag
        // @plugin dDiscordBot
        // @description
        // Returns the group that owns this channel.
        // -->
        registerTag("group", (attribute, object) -> {
            DiscordConnection.ChannelCache cache = object.getCacheChannel();
            if (cache != null) {
                return new DiscordGroupTag(object.bot, cache.guildId);
            }
            Channel chan = object.getChannel();
            Guild guild;
            if (chan instanceof GuildChannel) {
                guild = ((GuildChannel) chan).getGuild().block();
            }
            else {
                return null;
            }
            return new DiscordGroupTag(object.bot, guild);

        });
        // <--[tag]
        // @attribute <DiscordChannelTag.position>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @description
        // Returns the position of the channel.
        // -->
        registerTag("position", (attribute, object) -> {
            return new ElementTag(object.getChannel().getRestChannel().getData().block().position().get());
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.topic>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the topic of the channel.
        // -->
        registerTag("topic", (attribute, object) -> {
            return new ElementTag(object.getChannel().getRestChannel().getData().block().topic().get().get());
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.nsfw>
        // @returns ElementTag(Boolean)
        // @plugin dDiscordBot
        // @description
        // Returns whether or not the channel is marked NSFW.
        // -->
        registerTag("nsfw", (attribute, object) -> {
            return new ElementTag(object.getChannel().getRestChannel().getData().block().nsfw().get());
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.rate_limit_per_user>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @description
        // Returns the channels rate limit per user.
        // -->
        registerTag("rate_limit_per_user", (attribute, object) -> {
            return new ElementTag(object.getChannel().getRestChannel().getData().block().rateLimitPerUser().get());
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.bitrate>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @description
        // Returns the channels bitrate.
        // -->
        registerTag("bitrate", (attribute, object) -> {
            return new ElementTag(object.getChannel().getRestChannel().getData().block().bitrate().get());
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.user_limit>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @description
        // Returns the channels user limit.
        // -->
        registerTag("user_limit", (attribute, object) -> {
            return new ElementTag(object.getChannel().getRestChannel().getData().block().userLimit().get());
        });

    }

    public static ObjectTagProcessor<DiscordChannelTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTag(String name, TagRunnable.ObjectInterface<DiscordChannelTag> runnable, String... variants) {
        tagProcessor.registerTag(name, runnable, variants);
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    String prefix = "discordchannel";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>'  ");
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String getObjectType() {
        return "DiscordChannel";
    }

    @Override
    public String identify() {
        if (bot != null) {
            return "discordchannel@" + bot + "," + channel_id;
        }
        return "discordchannel@" + channel_id;
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        }
        return this;
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // <--[mechanism]
        // @object DiscordChannelTag
        // @name name
        // @input ElementTag(String)
        // @description
        // Sets the name of the discord channel
        // @tags
        // <DiscordChannelTag.name>
        // -->
        if (mechanism.matches("name")) {
            getChannel().getRestChannel().modify(ChannelModifyRequest.builder().name(mechanism.getValue().asString()).build(), null).block().name();
        }
        // <--[mechanism]
        // @object DiscordChannelTag
        // @name name
        // @input ElementTag(Number)
        // @description
        // Sets the position of the discord channel
        // @tags
        // <DiscordChannelTag.name>
        // -->
        if (mechanism.matches("position") && mechanism.requireInteger()) {
            getChannel().getRestChannel().modify(ChannelModifyRequest.builder().position(mechanism.getValue().asInt()).build(), null).block().position();
        }
        // <--[mechanism]
        // @object DiscordChannelTag
        // @name topic
        // @input ElementTag(String)
        // @description
        // Sets the topic of the discord channel
        // @tags
        // <DiscordChannelTag.topic>
        // -->
        if (mechanism.matches("topic")) {
            getChannel().getRestChannel().modify(ChannelModifyRequest.builder().topic(mechanism.getValue().asString()).build(), null).block().topic();
        }
        // <--[mechanism]
        // @object DiscordChannelTag
        // @name nsfw
        // @input ElementTag(Boolean)
        // @description
        // Set the channels NSFW status
        // @tags
        // <DiscordChannelTag.nsfw>
        // -->
        if (mechanism.matches("nsfw") && mechanism.requireBoolean()) {
            getChannel().getRestChannel().modify(ChannelModifyRequest.builder().nsfw(mechanism.getValue().asBoolean()).build(), null).block().nsfw();
        }
        // <--[mechanism]
        // @object DiscordChannelTag
        // @name rate_limit_per_user
        // @input ElementTag(Number)
        // @description
        // Set the channels rate limit per user
        // @tags
        // <DiscordChannelTag.rate_limit_per_user>
        // -->
        if (mechanism.matches("rate_limit_per_user") && mechanism.requireInteger()) {
            getChannel().getRestChannel().modify(ChannelModifyRequest.builder().rateLimitPerUser(mechanism.getValue().asInt()).build(), null).block().rateLimitPerUser();
        }
        // <--[mechanism]
        // @object DiscordChannelTag
        // @name bitrate
        // @input ElementTag(Number)
        // @description
        // Set the channels bitrate
        // @tags
        // <DiscordChannelTag.bitrate>
        // -->
        if (mechanism.matches("bitrate") && mechanism.requireInteger()) {
            getChannel().getRestChannel().modify(ChannelModifyRequest.builder().bitrate(mechanism.getValue().asInt()).build(), null).block().bitrate();
        }
        // <--[mechanism]
        // @object DiscordChannelTag
        // @name user_limit
        // @input ElementTag(Number)
        // @description
        // Set the channels user limit
        // @tags
        // <DiscordChannelTag.user_limit>
        // -->
        if (mechanism.matches("user_limit") && mechanism.requireInteger()) {
            getChannel().getRestChannel().modify(ChannelModifyRequest.builder().userLimit(mechanism.getValue().asInt()).build(), null).block().userLimit();
        }
    }

    @Override
    public void applyProperty(Mechanism mechanism) {

    }
}
