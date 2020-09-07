package com.denizenscript.ddiscordbot.events;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.event.domain.channel.VoiceChannelCreateEvent;

public class DiscordVoiceChannelDeletedScriptEvent extends DiscordScriptEvent {

    public static DiscordVoiceChannelDeletedScriptEvent instance;

    // <--[event]
    // @Events
    // discord voice channel deleted
    //
    // @Regex ^discord voice channel deleted$
    //
    // @Triggers when a voice channel is deleted
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.channel> returns the channel that was deleted
    // -->

    public VoiceChannelCreateEvent getEvent() {
        return (VoiceChannelCreateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord voice channel deleted");
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
        return "DiscordVoiceChannelDeleted";
    }
}
