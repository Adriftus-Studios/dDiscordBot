package com.denizenscript.ddiscordbot.events;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.event.domain.channel.VoiceChannelCreateEvent;
import discord4j.core.event.domain.channel.VoiceChannelUpdateEvent;

public class DiscordVoiceChannelUpdatedScriptEvent extends DiscordScriptEvent {

    public static DiscordVoiceChannelUpdatedScriptEvent instance;

    // <--[event]
    // @Events
    // discord voice channel updated
    //
    // @Regex ^discord voice channel updated$
    //
    // @Triggers when a voice channel is updated
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.old_channel> returns the channel before it was updated
    // <context.new_channel> returns the channel after it was updated
    // -->

    public VoiceChannelUpdateEvent getEvent() {
        return (VoiceChannelUpdateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord voice channel updated");
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
            return new DiscordChannelTag(botID, getEvent().getCurrent());
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordVoiceChannelUpdated";
    }
}
