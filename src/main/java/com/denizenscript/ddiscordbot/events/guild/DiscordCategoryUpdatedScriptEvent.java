package com.denizenscript.ddiscordbot.events.guild;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import discord4j.core.event.domain.channel.CategoryDeleteEvent;

public class DiscordCategoryUpdatedScriptEvent extends DiscordScriptEvent {

    public static DiscordCategoryUpdatedScriptEvent instance;

    // <--[event]
    // @Events
    // discord category updated
    //
    // @Regex ^discord category updated$
    //
    // @Group Discord
    //
    // @Triggers when a channel category is updated.
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.name> returns the name of the category
    // -->

    public CategoryDeleteEvent getEvent() {
        return (CategoryDeleteEvent) event;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("discord category updated");
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
        return "DiscordCategoryUpdated";
    }
}
