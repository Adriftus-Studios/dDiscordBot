package com.denizenscript.ddiscordbot.events.guild;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import discord4j.core.event.domain.channel.CategoryCreateEvent;

public class DiscordCategoryCreateScriptEvent extends DiscordScriptEvent {

    public static DiscordCategoryCreateScriptEvent instance;

    // <--[event]
    // @Events
    // discord category created
    //
    // @Regex ^discord category created$
    //
    // @Group Discord
    //
    // @Triggers when a channel category is created.
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.name> returns the name of the category
    // -->

    public CategoryCreateEvent getEvent() {
        return (CategoryCreateEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord category created");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("name")) {
            return new ElementTag(getEvent().getCategory().getName());
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordCategoryCreated";
    }
}
