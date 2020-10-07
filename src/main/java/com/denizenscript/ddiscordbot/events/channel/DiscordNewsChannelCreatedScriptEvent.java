package com.denizenscript.ddiscordbot.events.channel;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.event.domain.channel.NewsChannelCreateEvent;

public class DiscordNewsChannelCreatedScriptEvent extends DiscordScriptEvent {

    public static DiscordNewsChannelCreatedScriptEvent instance;

    // <--[event]
    // @Events
    // discord news channel created
    //
    // @Regex ^discord news channel created$
    //
    // @Group Discord
    //
    // @Triggers when a news channel is created
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.channel> returns the channel that was created
    // -->

    public NewsChannelCreateEvent getEvent() {
        return (NewsChannelCreateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord news channel created");
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
        return "DiscordNewsChannelCreated";
    }
}
