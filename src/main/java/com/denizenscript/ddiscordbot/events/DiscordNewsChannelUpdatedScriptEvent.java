package com.denizenscript.ddiscordbot.events;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.event.domain.channel.NewsChannelUpdateEvent;

public class DiscordNewsChannelUpdatedScriptEvent extends DiscordScriptEvent {

    public static DiscordNewsChannelUpdatedScriptEvent instance;

    // <--[event]
    // @Events
    // discord news channel updated
    //
    // @Regex ^discord news channel updated$
    //
    // @Triggers when a news channel is updated
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.old_channel> returns the channel before it was updated
    // <context.new_channel> returns the channel after it was updated
    // -->

    public NewsChannelUpdateEvent getEvent() {
        return (NewsChannelUpdateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord news channel updated");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("old_channel")) {
            return new DiscordChannelTag(botID, getEvent().getOld().get());
        }
        if (name.equals("new_channel")) {
            return new DiscordChannelTag(botID, getEvent().getNewsChannel().get());
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordNewsChannelUpdated";
    }
}
