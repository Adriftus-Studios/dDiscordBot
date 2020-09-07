package com.denizenscript.ddiscordbot.events;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordGroupTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import discord4j.core.event.domain.VoiceServerUpdateEvent;

public class DiscordVoiceServerUpdateScriptEvent extends DiscordScriptEvent {

    public static DiscordVoiceServerUpdateScriptEvent instance;

    // <--[event]
    // @Events
    // discord voice server updates
    //
    // @Regex ^discord voice server updates$
    //
    // @Triggers TODO
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.group> returns the DiscordGroupTag
    // <context.endpoint> returns the server's endpoint
    // <context.token> returns the user's auth token
    // -->

    public VoiceServerUpdateEvent getEvent() {
        return (VoiceServerUpdateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord voice server updates");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("group")) {
            return new DiscordGroupTag(botID, getEvent().getGuild().block());
        }
        if (name.equals("endpoint")) {
            return new ElementTag(getEvent().getEndpoint());
        }
        if (name.equals("token")) {
            return new ElementTag(getEvent().getToken());
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordVoiceServerUpdates";
    }
}
