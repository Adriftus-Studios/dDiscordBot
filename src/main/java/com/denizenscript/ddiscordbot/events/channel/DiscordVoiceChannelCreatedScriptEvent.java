package com.denizenscript.ddiscordbot.events.channel;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.event.domain.channel.VoiceChannelCreateEvent;

public class DiscordVoiceChannelCreatedScriptEvent extends DiscordScriptEvent {

    public static DiscordVoiceChannelCreatedScriptEvent instance;

    // <--[event]
    // @Events
    // discord voice channel created
    //
    // @Regex ^discord voice channel created$
    //
    // @Group Discord
    //
    // @Triggers when a voice channel is created
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.channel> returns the channel that was created
    // -->

    public VoiceChannelCreateEvent getEvent() {
        return (VoiceChannelCreateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord voice channel created");
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
        return "DiscordVoiceChannelCreated";
    }
}
