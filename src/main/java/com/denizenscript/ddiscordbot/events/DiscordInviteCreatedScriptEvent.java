package com.denizenscript.ddiscordbot.events;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.ddiscordbot.objects.DiscordChannelTag;
import com.denizenscript.ddiscordbot.objects.DiscordUserTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import discord4j.core.event.domain.InviteCreateEvent;

public class DiscordInviteCreatedScriptEvent extends DiscordScriptEvent {
    public static DiscordInviteCreatedScriptEvent instance;

    // <--[event]
    // @Events
    // discord invite created
    //
    // @Regex ^discord invite created$
    //
    // @Triggers when a category channel category was created.
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.code> returns the invite code
    // <context.author> returns the user who created the invite
    // <context.channel> returns the channel the invite was created for
    // -->

    public InviteCreateEvent getEvent() {
        return (InviteCreateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord invite created");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("code")) {
            return new ElementTag(getEvent().getCode());
        }
        if (name.equals("author")) {
            return new DiscordUserTag(botID, getEvent().getInviter().get());
        }
        if (name.equals("channel")) {
            return new DiscordChannelTag(botID, getEvent().getChannelId().asLong());
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordInviteCreated";
    }
}
