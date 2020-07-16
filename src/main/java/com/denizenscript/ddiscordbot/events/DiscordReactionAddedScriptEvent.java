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
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.Iterator;

public class DiscordReactionAddedScriptEvent extends DiscordScriptEvent {
    public static DiscordReactionAddedScriptEvent instance;

    public ReactionAddEvent getEvent() {
        return (ReactionAddEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord reaction added");
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
        else if (name.equals("emoji_id")) {
            return new ElementTag(getEvent().getEmoji().asCustomEmoji().get().getId().asString());
        }
        else if (name.equals("emoji")) {
            return new ElementTag(getEvent().getEmoji().asCustomEmoji().get().getName());
        }
        else if (name.equals("message")) {
            return new ElementTag(getEvent().getMessage().block().getContent());
        }
        else if (name.equals("message_id")) {
            return new ElementTag(getEvent().getMessage().block().getId().asString());
        }
        else if (name.equals("formatted_message")) {
            // NEW
            String m = getEvent().getMessage().block().getContent();
            Iterator<User> it = getEvent().getMessage().block().getUserMentions().toIterable().iterator();
            // replacing usernames
            while (it.hasNext()) {
                User u = it.next();
                m = m.replaceAll("(<[^<>$]!?" + u.getId().asString() + ">)", "@" + u.getUsername());
            }
            // replacing channel names and role names
            for (String s : m.split("\\s+")) {
                if (s.matches("(^<#[0-9]{18}>$)")) {
                    String str = s.replace("<#", "").replace(">", "");
                    m = m.replaceAll("(<[^<>$]" + str + ">)", "#" + ((GuildChannel) DenizenDiscordBot.instance.connections.get(botID).client.getChannelById(Snowflake.of(str)).block()).getName().replaceAll("[^a-zA-Z-]", ""));
                }
                if (s.matches("(^<@&[0-9]{18}>$)")) {
                    String str = s.replace("<@&", "").replace(">", "");
                    m = m.replaceAll("(<@[^<>$]" + str + ">)", "@" + (DenizenDiscordBot.instance.connections.get(botID).client.getRoleById(getEvent().getGuildId().get(), Snowflake.of(str)).block()).getName().replaceAll("[^\\Wa-zA-Z-]", ""));
                }
            }
            return new ElementTag(m);
        }
        else if (name.equals("attachments")) {
            // ALSO NEW
            if (getEvent().getMessage().block().getAttachments().size() != 0) {
                ListTag list = new ListTag();
                for (Attachment att : getEvent().getMessage().block().getAttachments()) {
                    list.addObject(new ElementTag(att.getUrl()));
                }
                return list;
            }
        }
        else if (name.equals("urls")) {
            ListTag list = new ListTag();
            for (String s : getEvent().getMessage().block().getContent().split("\\s+")) {
                if (s.matches("(https?://)[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")) {
                    list.addObject(new ElementTag(s));
                }
            }
            if(list.size() != 0) {
                return list;
            }
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
        return "DiscordReactionAdded";
    }
}
