package com.denizenscript.ddiscordbot.events;

import com.denizenscript.ddiscordbot.DenizenDiscordBot;
import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.ddiscordbot.objects.DiscordGroupTag;
import com.denizenscript.ddiscordbot.objects.DiscordUserTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.Iterator;

public class DiscordReactionRemovedScriptEvent  extends DiscordScriptEvent {
    public static DiscordReactionRemovedScriptEvent instance;

    // <--[event]
    // @Events
    // discord reaction removed
    //
    // @Regex ^on discord reaction removed$
    //
    // @Switch for:<bot> to only process the event for a specified Discord bot.
    // @Switch channel:<channel_id> to only process the event when it occurs in a specified Discord channel.
    // @Switch group:<group_id> to only process the event for a specified Discord group.
    //
    // @Triggers when a Discord user removes a reaction from a message.
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.bot> returns the relevant Discord bot object.
    // <context.channel> returns the channel.
    // <context.group> returns the group.
    // <context.message> returns the message (raw).
    // <context.message_id> returns the message ID.
    // <context.custom> returns true if the emoji is a custom emoji.
    // <context.emoji_id> returns the ID of a custom emoji
    // <context.emoji> returns the unicode emoji, or name of the custom emoji.
    // <context.author> returns the user that added the reaction.
    //
    // -->

    public ReactionRemoveEvent getEvent() {
        return (ReactionRemoveEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord reaction removed");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.checkSwitch("channel", String.valueOf(getEvent().getMessage().block().getChannelId().asLong()))) {
            return false;
        }
        if (getEvent().getGuildId().isPresent() && !path.checkSwitch("group", String.valueOf(getEvent().getGuildId().get().asLong()))) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("channel")) {
            return new DiscordChannelTag(botID, getEvent().getMessage().block().getChannelId().asLong());
        }
        else if (name.equals("group")) {
            if (getEvent().getGuildId().isPresent()) {
                return new DiscordGroupTag(botID, getEvent().getGuildId().get().asLong());
            }
        }
        else if (name.equals("author")) {
            return new DiscordUserTag(botID, getEvent().getUser().block());
        }
        else if (name.equals("custom")) {
            return new ElementTag(getEvent().getEmoji().asCustomEmoji().isPresent());
        }
        else if (name.equals("emoji_id")) {
            return new ElementTag(getEvent().getEmoji().asCustomEmoji().get().getId().asString());
        }
        else if (name.equals("emoji")) {
            if (getEvent().getEmoji().asCustomEmoji().isPresent()) {
                return new ElementTag(getEvent().getEmoji().asCustomEmoji().get().getName());
            }
            return new ElementTag(getEvent().getEmoji().asUnicodeEmoji().get().getRaw());
        }
        else if (name.equals("message_id")) {
            return new ElementTag(getEvent().getMessage().block().getId().asString());
        }
        else if (name.equals("channel_name")) {
            DenizenDiscordBot.userContextDeprecation.warn();
            MessageChannel channel = getEvent().getMessage().block().getChannel().block();
            if (channel instanceof GuildChannel) {
                return new ElementTag(((GuildChannel) channel).getName());
            }
        }
        else if (name.equals("group_name")) {
            DenizenDiscordBot.userContextDeprecation.warn();
            if (getEvent().getGuildId().isPresent()) {
                return new ElementTag(getEvent().getGuild().block().getName());
            }
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordReactionRemoved";
    }
}
