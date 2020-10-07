package com.denizenscript.ddiscordbot.events.channel;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.event.domain.channel.NewsChannelDeleteEvent;

public class DiscordNewsChannelDeletedScriptEvent extends DiscordScriptEvent {

    public static DiscordNewsChannelDeletedScriptEvent instance;

    // <--[event]
    // @Events
    // discord news channel deleted
    //
    // @Regex ^discord news channel deleted$
    //
    // @Group Discord
    //
    // @Triggers when a news channel is deleted
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.channel> returns the channel that was deleted
    // -->

    public NewsChannelDeleteEvent getEvent() {
        return (NewsChannelDeleteEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord news channel deleted");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("channel")) {
            return new DiscordChannelTag(botID, getEvent().getChannel());
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordNewsChannelDeleted";
    }
}
