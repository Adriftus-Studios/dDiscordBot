package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.ddiscordbot.DenizenDiscordBot;
import com.denizenscript.ddiscordbot.DiscordConnection;
import com.denizenscript.denizen.objects.notable.NotableManager;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.notable.Notable;
import com.denizenscript.denizencore.objects.notable.Note;
import com.denizenscript.denizencore.tags.*;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.*;
import org.bukkit.Bukkit;

public class DiscordChannelTag implements ObjectTag, Adjustable, Notable {

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
        Notable noted = NotableManager.getSavedObject(string);
        if (noted instanceof DiscordChannelTag) {
            return (DiscordChannelTag) noted;
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
        this.channel = getBot().client.getChannelById(Snowflake.of(channel_id)).block();
    }

    public DiscordChannelTag(String bot, Channel channel) {
        this.bot = bot;
        this.channel = channel;
        this.channel_id = channel.getId().asLong();
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

    public Channel channel;

    public String bot;

    public long channel_id;

    public static void registerTags() {

        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            public void run(ReplaceableTagEvent event) {
                if (event.matches("discordchannel") && !event.replaced()) {
                    DiscordChannelTag tag = null;
                    if (event.hasNameContext()) {
                        String context = event.getNameContext();
                        tag = valueOf(context, event.getContext());
                    }
                    if (tag != null) {
                        event.setReplacedObject(CoreUtilities.autoAttrib(tag, event.getAttributes().fulfill(1)));
                    }
                }
            }
        }, "discordchannel");

        // <--[tag]
        // @attribute <DiscordChannelTag.name>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @mechanism DiscordChannelTag.name
        // @description
        // Returns the name of the channel.
        // -->
        registerTag("name", (attribute, object) -> {
            DiscordConnection.ChannelCache cache = object.getCacheChannel();
            if (cache != null) {
                return new ElementTag(cache.name);
            }
            Channel chan = object.channel;
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
            return new ElementTag(object.channel.getType().name());

        });

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
            Channel chan = object.channel;
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
        // @mechanism DiscordChannelTag.position
        // @description
        // Returns the position of the channel.
        // -->
        registerTag("position", (attribute, object) -> {
            return new ElementTag(((GuildChannel) object.channel).getPosition().block());
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.topic>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @mechanism DiscordChannelTag.topic
        // @description
        // Returns the topic of the channel.
        // -->
        registerTag("topic", (attribute, object) -> {
            if (object.channel.getType() == Channel.Type.GUILD_TEXT) {
                TextChannel channel = (TextChannel) object.channel;
                if (channel.getTopic().isPresent()) {
                    return new ElementTag(channel.getTopic().get());
                }
                return new ElementTag("");
            }
            return null;
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.nsfw>
        // @returns ElementTag(Boolean)
        // @plugin dDiscordBot
        // @mechanism DiscordChannelTag.nsfw
        // @description
        // Returns whether or not the channel is marked NSFW.
        // -->
        registerTag("nsfw", (attribute, object) -> {
            if (object.channel.getType() == Channel.Type.GUILD_TEXT) {
                TextChannel channel = (TextChannel) object.channel;
                if (channel.getTopic().isPresent()) {
                    return new ElementTag(channel.isNsfw());
                }
                return new ElementTag("");
            }
            return null;
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.rate_limit_per_user>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @mechanism DiscordChannelTag.rate_limit_per_user
        // @description
        // Returns the channels rate limit per user.
        // -->
        registerTag("rate_limit_per_user", (attribute, object) -> {
            if (object.channel.getType() == Channel.Type.GUILD_TEXT) {
                TextChannel channel = (TextChannel) object.channel;
                if (channel.getTopic().isPresent()) {
                    return new ElementTag(channel.getRateLimitPerUser());
                }
                return new ElementTag("");
            }
            return null;
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.bitrate>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @mechanism DiscordChannelTag.bitrate
        // @description
        // Returns the channels bitrate.
        // -->
        registerTag("bitrate", (attribute, object) -> {
            if (object.channel.getType() == Channel.Type.GUILD_VOICE) {
                VoiceChannel channel = (VoiceChannel) object.channel;
                return new ElementTag(channel.getBitrate());
            }
            return null;
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.user_limit>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @mechanism DiscordChannelTag.user_limit
        // @description
        // Returns the channels user limit.
        // -->
        registerTag("user_limit", (attribute, object) -> {
            if (object.channel.getType() == Channel.Type.GUILD_VOICE) {
                VoiceChannel channel = (VoiceChannel) object.channel;
                return new ElementTag(channel.getUserLimit());
            }
            return null;
        });

        // <--[tag]
        // @attribute <DiscordChannelTag.is_private>
        // @returns ElementTag(Number)
        // @plugin dDiscordBot
        // @description
        // Returns whether or not the channel is private (direct message).
        // -->
        registerTag("is_private", (attribute, object) -> {
            return new ElementTag(object.channel instanceof PrivateChannel);
        });

        registerTag("private", (attribute, object) -> {
            attribute = attribute.fulfill(1);

            if (!(object.channel instanceof PrivateChannel)) {
                return null;
            }
            PrivateChannel c = (PrivateChannel) object.channel;
            // <--[tag]
            // @attribute <DiscordChannelTag.private.users>
            // @returns DiscordUserTag
            // @plugin dDiscordBot
            // @description
            // Returns a list of all users in the private channel.
            // -->
            if (attribute.startsWith("users")) {
                ListTag users = new ListTag();
                for (User u : c.getRecipients().collectList().block()) {
                    users.addObject(new DiscordUserTag(object.bot, u));
                }
                return users;
            }
            return null;
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
        for (DiscordChannelTag saved : NotableManager.getAllType(DiscordChannelTag.class)) {
            if (saved.channel_id != this.channel_id) {
                continue;
            }
            return true;
        }
        return false;
    }

    public void makeUnique(String id) {
        NotableManager.saveAs(this, id);
    }

    @Note("DiscordChannels")
    public String getSaveObject() {
        if (bot != null) {
            return bot + "," + channel_id;
        }
        return String.valueOf(channel_id);
    }

    public void forget() {
        NotableManager.remove(this);
    }

    @Override
    public String getObjectType() {
        return "DiscordChannel";
    }

    @Override
    public String identify() {
        String id = "discordchannel@";
        if (bot != null) {
            return id + bot + ","  + channel_id;
        }
        return bot + "," + channel_id;
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
        // using runnables to prevent mech server lag
        Bukkit.getScheduler().runTask(DenizenDiscordBot.instance, new Runnable() {
            @Override
            public void run() {
                // <--[mechanism]
                // @object DiscordChannelTag
                // @name name
                // @input ElementTag
                // @description
                // Sets the name of the discord channel
                // @tags
                // <DiscordChannelTag.name>
                // -->
                if (mechanism.matches("name")) {
                    if(channel instanceof TextChannel) {
                        ((TextChannel)channel).edit(e -> {
                            e.setName(mechanism.getValue().asString());
                        }).block();
                    } else if(channel instanceof VoiceChannel) {
                        ((VoiceChannel)channel).edit(e -> {
                            e.setName(mechanism.getValue().asString());
                        }).block();
                    }
                }
                // <--[mechanism]
                // @object DiscordChannelTag
                // @name position
                // @input ElementTag(Number)
                // @description
                // Sets the position of the discord channel
                // @tags
                // <DiscordChannelTag.position>
                // -->
                if (mechanism.matches("position") && mechanism.requireInteger()) {
                    if(channel instanceof TextChannel) {
                        ((TextChannel)channel).edit(e -> {
                            e.setPosition(mechanism.getValue().asInt());
                        }).block();
                    } else if(channel instanceof VoiceChannel) {
                        ((VoiceChannel)channel).edit(e -> {
                            e.setPosition(mechanism.getValue().asInt());
                        }).block();
                    }
                }
                // <--[mechanism]
                // @object DiscordChannelTag
                // @name topic
                // @input ElementTag
                // @description
                // Sets the topic of the discord channel
                // @tags
                // <DiscordChannelTag.topic>
                // -->
                if (mechanism.matches("topic")) {
                    if(channel instanceof TextChannel) {
                        ((TextChannel)channel).edit(e -> {
                            e.setTopic(mechanism.getValue().asString());
                        }).block();
                    }
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
                    if(channel instanceof TextChannel) {
                        ((TextChannel)channel).edit(e -> {
                            e.setNsfw(mechanism.getValue().asBoolean());
                        }).block();
                    }
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
                    if(channel instanceof TextChannel) {
                        ((TextChannel)channel).edit(e -> {
                            e.setRateLimitPerUser(mechanism.getValue().asInt());
                        }).block();
                    }
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
                    if(channel instanceof VoiceChannel) {
                        ((VoiceChannel)channel).edit(e -> {
                            e.setBitrate(mechanism.getValue().asInt());
                        }).block();
                    }
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
                    if(channel instanceof VoiceChannel) {
                        ((VoiceChannel)channel).edit(e -> {
                            e.setUserLimit(mechanism.getValue().asInt());
                        }).block();
                    }
                }
            }
        });
    }

    @Override
    public void applyProperty(Mechanism mechanism) {

    }
}
