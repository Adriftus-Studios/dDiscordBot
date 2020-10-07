package com.denizenscript.ddiscordbot.events.voice;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.ddiscordbot.objects.DiscordUserTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import discord4j.core.event.domain.VoiceStateUpdateEvent;

public class DiscordVoiceStateUpdateScriptEvent   extends DiscordScriptEvent {

    public static DiscordVoiceStateUpdateScriptEvent instance;

    // <--[event]
    // @Events
    // discord voice state updates
    //
    // @Regex ^discord voice state updates$
    //
    // @Group Discord
    //
    // @Triggers when a User joins or leaves a voice channel.
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.user> returns the User that Joined/Left the channel
    // <context.channel_left> returns the Channel that the user left, if any.
    // <context.channel_joined> returns the Channel that the user joined, if any.
    // -->

    public VoiceStateUpdateEvent getEvent() {
        return (VoiceStateUpdateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord voice state updates");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("channel_left")) {
            return new DiscordChannelTag(botID, getEvent().getOld().orElseGet(null).getChannelId().get().asLong());
        }
        if (name.equals("channel_joined")) {
            return new DiscordChannelTag(botID, getEvent().getCurrent().getChannelId().orElseGet(null).asLong());
        }
        if (name.equals("user")) {
            return new DiscordUserTag(botID, getEvent().getCurrent().getUser().block().getId().asLong());
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordVoiceStateUpdates";
    }
}
