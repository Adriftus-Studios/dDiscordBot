package com.denizenscript.ddiscordbot.events.message;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.denizenscript.ddiscordbot.DenizenDiscordBot;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.ddiscordbot.objects.DiscordGroupTag;
import com.denizenscript.ddiscordbot.objects.DiscordMessageTag;
import com.denizenscript.ddiscordbot.objects.DiscordUserTag;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.Channel;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.common.util.Snowflake;

public class DiscordMessageReceivedScriptEvent extends DiscordScriptEvent {

    public static DiscordMessageReceivedScriptEvent instance;

    // <--[event]
    // @Events
    // discord message received
    //
    // @Regex ^on discord message received$
    //
    // @Switch for:<bot> to only process the event for a specified Discord bot.
    // @Switch channel:<channel_id> to only process the event when it occurs in a specified Discord channel.
    // @Switch group:<group_id> to only process the event for a specified Discord group.
    //
    // @Group Discord
    //
    // @Cancellable true
    //
    // @Triggers when a Discord bot receives a message.
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.bot> returns the relevant Discord bot object.
    // <context.channel> returns the DiscordChannelTag.
    // <context.group> returns the DiscordGroupTag.
    // <context.message> returns the DiscordMessageTag.
    // <context.no_mention_message> returns the message with all user mentions stripped.
    // <context.formatted_message> returns the formatted message (mentions/etc. are written cleanly).
    // <context.attachments> returns a list with URLs for all attachments, returns null if no attachments.
    // <context.urls> returns a list of all URLs in the message, returns null if the message contains no URLs.
    // <context.author> returns the user that authored the message (may not be valid, eg for a Webhook post).
    // <context.mentions> returns a list of all mentioned users.
    // <context.is_direct> returns whether the message was sent directly to the bot (if false, the message was sent to a public channel).
    //
    // -->

    public MessageCreateEvent getEvent() {
        return (MessageCreateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord message received");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.checkSwitch("channel", String.valueOf(getEvent().getMessage().getChannelId().asLong()))) {
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
            return new DiscordChannelTag(botID, getEvent().getMessage().getChannelId().asLong());
        }
        else if (name.equals("group")) {
            if (getEvent().getGuildId().isPresent()) {
                return new DiscordGroupTag(botID, getEvent().getGuildId().get().asLong());
            }
        }
        else if (name.equals("message")) {
            return new DiscordMessageTag(botID, getEvent().getMessage());
        }
        else if (name.equals("no_mention_message")) {
            return new ElementTag(stripMentions(getEvent().getMessage().getContent(),
                    getEvent().getMessage().getUserMentions()));
        }
        else if (name.equals("formatted_message")) {
            String m = getEvent().getMessage().getContent();
            List<User> users = getEvent().getMessage().getUserMentions().collectList().block();
            for (User user : users) {
                m = m.replace(user.getMention(), "@" + user.getUsername() + "#" + user.getDiscriminator());
                // Discord is stupid, and adds a "!" for Nitro descriminators, and also has different output for the getUsername method.
                m = m.replace(user.getMention().substring(0,2) + "!" + user.getMention().substring(2), "@" + user.getUsername());
            }
            List<Role> roles = getEvent().getMessage().getRoleMentions().collectList().block();
            for (Role role : roles) {
                m = m.replace(role.getMention(), "@" + role.getName());
            }
            // Discord API does not reliably offer a better method for this.
            final Pattern CHANNEL_PATTERN = Pattern.compile("<#([0-9]+)>");
            Matcher matcher = CHANNEL_PATTERN.matcher(m);
            while (matcher.find()) {
                String channelID = matcher.group(1);
                Channel channel = getEvent().getClient().getChannelById(Snowflake.of(channelID)).block();
                m = m.replace(channel.getMention(), "#" + ((GuildChannel) channel).getName());
            }
            return new ElementTag(m);
        }
        else if (name.equals("attachments")) {
            ListTag list = new ListTag();
            for (Attachment att : getEvent().getMessage().getAttachments()) {
                list.addObject(new ElementTag(att.getUrl()));
            }
            return list;
        }
        else if (name.equals("urls")) {
            ListTag list = new ListTag();
            for (String s : getEvent().getMessage().getContent().split("\\s+")) {
                if (s.matches("(https?://)[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")) {
                    list.addObject(new ElementTag(s));
                }
            }
            return list;
        }
        else if (name.equals("author")) {
            if (!getEvent().getMessage().getAuthor().isPresent()) {
                return null;
        }
            return new DiscordUserTag(botID, getEvent().getMessage().getAuthor().get());
        }
        else if (name.equals("mentions")) {
            DenizenDiscordBot.userContextDeprecation.warn();
            ListTag list = new ListTag();
            for (Snowflake user : getEvent().getMessage().getUserMentionIds()) {
                list.addObject(new DiscordUserTag(botID, user.asLong()));
            }
            return list;
        }
        else if (name.equals("is_direct")) {
            return new ElementTag(!(getEvent().getMessage().getChannel().block() instanceof GuildChannel));
        }
        else if (name.equals("mention_names")) {
            DenizenDiscordBot.userContextDeprecation.warn();
            ListTag list = new ListTag();
            for (User user : getEvent().getMessage().getUserMentions().toIterable()) {
                list.add(String.valueOf(user.getUsername()));
            }
            return list;
        }
        return super.getContext(name);
    }

    @Override
    public boolean applyDetermination(ScriptPath path, ObjectTag determinationObj) {
        if (determinationObj.toString().equalsIgnoreCase("cancelled")) {
            getEvent().getMessage().delete().block();
            return true;
        }
//        else if (determinationObj.toString().toLowerCase().startsWith("message:")) {
//            getEvent().getMessage().edit(c -> {
//                c.setContent(determinationObj.toString().substring(8));
//            }).block();
//        } TODO: fix error "Cannot edit a message authored by another user"
        return super.applyDetermination(path, determinationObj);
    }

    @Override
    public String getName() {
        return "DiscordMessageReceived";
    }
}
