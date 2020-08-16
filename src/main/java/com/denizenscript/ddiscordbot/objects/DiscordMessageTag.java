package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.ddiscordbot.DenizenDiscordBot;
import com.denizenscript.ddiscordbot.DiscordConnection;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.*;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordMessageTag implements ObjectTag, Adjustable {

    @Fetchable("discordmessage")
    public static DiscordMessageTag valueOf(String string, TagContext context) {
        if (string.startsWith("discordmessage@")) {
            string = string.substring("discordmessage@".length());
        }
        if (string.contains("@")) {
            return null;
        }
        try {
            String bot = string.split(",")[0];
            long chanID = Long.parseLong(string.split(",")[1]);
            long msgID = Long.parseLong(string.split(",")[2]);
            return new DiscordMessageTag(bot, chanID, msgID);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static boolean matches(String arg) {
        if (arg.startsWith("discordmessage@")) {
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

    public DiscordMessageTag(String bot, long channelId, long messageId) {
        this.bot = bot;
        this.channel = getBot().client.getChannelById(Snowflake.of(channelId)).block();
        this.message = getBot().client.getMessageById(Snowflake.of(channelId), Snowflake.of(messageId)).block();
        this.channel_id = channelId;
        this.message_id = messageId;
    }

    public DiscordMessageTag(String bot, Message message) {
        this.bot = bot;
        this.message = message;
        this.channel = message.getChannel().block();
        this.message_id = message.getId().asLong();
        this.channel_id = channel.getId().asLong();
    }

    public DiscordConnection getBot() {
        return DenizenDiscordBot.instance.connections.get(bot);
    }

    public Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        channel = getBot().client.getChannelById(Snowflake.of(channel_id)).block();
        return channel;
    }

    public Message getMessage() {
        if (message != null) {
            return message;
        }
        message = getBot().client.getMessageById(Snowflake.of(channel_id), Snowflake.of(message_id)).block();
        return message;
    }

    public Channel channel;

    public Message message;

    public String bot;

    public long channel_id;

    public long message_id;

    public static void registerTags() {

        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            public void run(ReplaceableTagEvent event) {
                if (event.matches("discordemoji") && !event.replaced()) {
                    DiscordEmojiTag tag = null;
                    String context = event.getNameContext().replace("discordemoji[", "").replace("]", "");
                    if (event.hasNameContext() && DiscordEmojiTag.matches(context)) {
                        String bot = context.split(",")[0];
                        String type = context.split(",")[1];
                        String id = context.split(",")[2];
                        if (type == "unicode") {
                            tag = new DiscordEmojiTag(bot, ReactionEmoji.unicode(id));
                        } else if (type.equalsIgnoreCase("custom")) {
                            String name = context.split(",")[3];
                            String animated = context.split(",")[4];
                            tag = new DiscordEmojiTag(bot, ReactionEmoji.custom(Snowflake.of(Long.parseLong(id)), name, Boolean.valueOf(animated)));
                        }
                    }

                    Attribute attribute = event.getAttributes().fulfill(1);

                    if (tag != null) {
                        event.setReplacedObject(CoreUtilities.autoAttrib(tag, attribute));
                    }
                }
            }
        }, "discordemoji");

        // <--[tag]
        // @attribute <DiscordMessageTag.id>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the ID of the message.
        // -->
        registerTag("id", (attribute, object) -> {
            String id;
            if (object.message != null) {
                id = object.message.getId().asString();
            } else {
                id = String.valueOf(object.message_id);
            }
            return new ElementTag(id);
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.message>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the contents of the message.
        // -->
        registerTag("message", (attribute, object) -> {
            String contents;
            if (object.message != null) {
                contents = object.message.getContent();
            } else {
                contents = String.valueOf(object.message.getContent());
            }
            return new ElementTag(contents);
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.author>
        // @returns DiscordUserTag
        // @plugin dDiscordBot
        // @description
        // Returns the author of the message.
        // -->
        registerTag("author", (attribute, object) -> {
            return new DiscordUserTag(object.bot, object.getMessage().getAuthor().get());
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.channel>
        // @returns DiscordChannelTag
        // @plugin dDiscordBot
        // @description
        // Returns the channel that contains the message.
        // -->
        registerTag("channel", (attribute, object) -> {
            return new DiscordChannelTag(object.bot, object.getMessage().getChannel().block());
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.group>
        // @returns DiscordGroupTag
        // @plugin dDiscordBot
        // @description
        // Returns the group that the message is in.
        // -->
        registerTag("group", (attribute, object) -> {
            return new DiscordGroupTag(object.bot, object.message.getGuild().block());
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.formatted_message>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // returns the formatted message (mentions/etc. are written cleanly).
        // -->
        registerTag("formatted_message", (attribute, object) -> {
            String m = object.getMessage().getContent();
            List<User> users = object.getMessage().getUserMentions().collectList().block();
            for (User user : users) {
                m = m.replace(user.getMention(), "@" + user.getUsername() + "#" + user.getDiscriminator());
                // Discord is stupid, and adds a "!" for Nitro descriminators, and also has different output for the getUsername method.
                m = m.replace(user.getMention().substring(0,2) + "!" + user.getMention().substring(2), "@" + user.getUsername());
            }
            List<Role> roles = object.getMessage().getRoleMentions().collectList().block();
            for (Role role : roles) {
                m = m.replace(role.getMention(), "@" + role.getName());
            }
            // Discord API does not reliably offer a better method for this.
            final Pattern CHANNEL_PATTERN = Pattern.compile("<#([0-9]+)>");
            Matcher matcher = CHANNEL_PATTERN.matcher(m);
            while (matcher.find()) {
                String channelID = matcher.group(1);
                Channel channel = object.getBot().client.getChannelById(Snowflake.of(channelID)).block();
                m = m.replace(channel.getMention(), "#" + ((GuildChannel) channel).getName());
            }
            return new ElementTag(m);
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.mentions>
        // @returns ListTag(DiscordUserTag)
        // @plugin dDiscordBot
        // @description
        // Returns a list of users mentioned in the message.
        // -->
        registerTag("mentions", (attribute, object) -> {
            ListTag list = new ListTag();
            for (Snowflake u : object.getMessage().getUserMentionIds()) {
                list.addObject(new DiscordUserTag(object.bot, object.getBot().client.getUserById(u).block()));
            }
            return list;
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.emojis>
        // @returns ListTag(DiscordEmojiTag)
        // @plugin dDiscordBot
        // @description
        // Returns a list of emojis used in the message.
        // Only works with guild emojis
        // -->
        registerTag("emojis", (attribute, object) -> {
            ListTag list = new ListTag();
            object.getBot().client.getGuildEmojis(object.message.getGuild().block().getId()).toStream().forEach((emoji) -> {
                if(object.message.getContent().contains(emoji.asFormat())) {
                    list.addObject(new DiscordEmojiTag(object.bot, ReactionEmoji.custom(emoji.getId(), emoji.getName(), emoji.isAnimated())));
                }
            });
            return list;
        });

        // <--[tag]
        // @attribute <DiscordMessageTag.reactions>
        // @returns ListTag(DiscordEmojiTag)
        // @plugin dDiscordBot
        // @description
        // Returns a map of DiscordEmojiTags with their counts.
        // -->
        registerTag("reactions", (attribute, object) -> {
            ListTag list = new ListTag();
            object.message.getReactions().stream().forEach((obj) -> {
                list.addObject(new DiscordEmojiTag(object.bot, obj.getEmoji()));
            });
            return list;
        });
    }

    public static ObjectTagProcessor<DiscordMessageTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTag(String name, TagRunnable.ObjectInterface<DiscordMessageTag> runnable, String... variants) {
        tagProcessor.registerTag(name, runnable, variants);
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    String prefix = "discordmessage";

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
        return "DiscordMessage";
    }

    @Override
    public String identify() {
        if (bot != null) {
            return "discordmessage@" + bot + "," + channel_id + "," + message_id;
        }
        return "discordmessage@" + channel_id + "," + message_id;
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
        // @object DiscordMessageTag
        // @name add_reaction
        // @input DiscordEmojiTag
        // @description
        // Forces the bot to add a reaction to a message
        // @tags
        // <DiscordMessageTag.reactions>
        // -->
        if (mechanism.matches("add_reaction")) {
            message.addReaction(DiscordEmojiTag.valueOf(mechanism.getValue().asString(), null).emoji).block();
        }
        // <--[mechanism]
        // @object DiscordMessageTag
        // @name remove_reaction
        // @input DiscordEmojiTag
        // @description
        // Forces the bot to remove a reaction from a message
        // @tags
        // <DiscordMessageTag.reactions>
        // -->
        if (mechanism.matches("remove_reaction")) {
            User connection = DenizenDiscordBot.instance.connections.get(bot).client.getSelf().block();
            message.removeReaction(DiscordEmojiTag.valueOf(mechanism.getValue().asString(), null).emoji, connection.getId()).block();
        }
        // <--[mechanism]
        // @object DiscordMessageTag
        // @name clear_reactions
        // @input none
        // @description
        // Removes all reactions from a message
        // @tags
        // <DiscordMessageTag.reactions>
        // -->
        if (mechanism.matches("clear_reactions")) {
            message.removeAllReactions().block();
        }
    }

    @Override
    public void applyProperty(Mechanism mechanism) {

    }
}
