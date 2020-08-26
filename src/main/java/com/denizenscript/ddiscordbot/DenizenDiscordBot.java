package com.denizenscript.ddiscordbot;

import com.denizenscript.ddiscordbot.events.*;
import com.denizenscript.ddiscordbot.objects.*;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.SlowWarning;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class DenizenDiscordBot extends JavaPlugin {

    public static SlowWarning userContextDeprecation = new SlowWarning("'user_id', 'author_name', and similar contexts are deprecated: use 'context.user.id' and similar.");

    public static DenizenDiscordBot instance;

    public HashMap<String, DiscordConnection> connections = new HashMap<>();

    @Override
    public void onEnable() {
        Debug.log("dDiscordBot loaded!");
        instance = this;
        try {
            DenizenCore.getCommandRegistry().registerCommand(DiscordCommand.class);
            ScriptEvent.registerScriptEvent(DiscordMessageModifiedScriptEvent.instance = new DiscordMessageModifiedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordMessageDeletedScriptEvent.instance = new DiscordMessageDeletedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordMessageReceivedScriptEvent.instance = new DiscordMessageReceivedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordUserJoinsScriptEvent.instance = new DiscordUserJoinsScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordUserLeavesScriptEvent.instance = new DiscordUserLeavesScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordUserRoleChangeScriptEvent.instance = new DiscordUserRoleChangeScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordReactionAddedScriptEvent.instance = new DiscordReactionAddedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordReactionRemovedScriptEvent.instance = new DiscordReactionRemovedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordVoiceStateUpdateScriptEvent.instance = new DiscordVoiceStateUpdateScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordInviteCreatedScriptEvent.instance = new DiscordInviteCreatedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordCategoryCreateScriptEvent.instance = new DiscordCategoryCreateScriptEvent());
            ObjectFetcher.registerWithObjectFetcher(DiscordChannelTag.class, DiscordChannelTag.tagProcessor);
            ObjectFetcher.registerWithObjectFetcher(DiscordBotTag.class, DiscordBotTag.tagProcessor);
            ObjectFetcher.registerWithObjectFetcher(DiscordGroupTag.class, DiscordGroupTag.tagProcessor);
            ObjectFetcher.registerWithObjectFetcher(DiscordRoleTag.class, DiscordRoleTag.tagProcessor);
            ObjectFetcher.registerWithObjectFetcher(DiscordUserTag.class, DiscordUserTag.tagProcessor);
            ObjectFetcher.registerWithObjectFetcher(DiscordMessageTag.class, DiscordMessageTag.tagProcessor);
            ObjectFetcher.registerWithObjectFetcher(DiscordEmojiTag.class, DiscordEmojiTag.tagProcessor);
            ObjectFetcher.registerWithObjectFetcher(DiscordEmbedTag.class, DiscordEmbedTag.tagProcessor);
            TagManager.registerTagHandler(new TagRunnable.RootForm() {
                @Override
                public void run(ReplaceableTagEvent event) {
                    discordTagBase(event);
                }
            }, "discord");
            // <--[tag]
            // @attribute <ElementTag.urls>
            // @returns ListTag
            // @plugin dDiscordBot
            // @description
            // Returns a list of URLs in the element.
            // Returns empty list if no URLs are found.
            // -->
            ElementTag.registerTag("urls", (attribute, object) -> {
                ListTag list = new ListTag();
                for (String s : object.asString().split("\\s+")) {
                    if (s.matches("(https?://)[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")) {
                        list.addObject(new ElementTag(s));
                    }
                }
                return list;
            });
        }
        catch (Throwable ex) {
            Debug.echoError(ex);
        }
    }

    public void discordTagBase(ReplaceableTagEvent event) {
        if (!event.matches("discord") || event.replaced()) {
            return;
        }

        DiscordBotTag bot = null;

        if (event.hasNameContext()) {
            bot = DiscordBotTag.valueOf(event.getNameContext(), event.getAttributes().context);
        }

        Attribute attribute = event.getAttributes().fulfill(1);

        // <--[tag]
        // @attribute <discord[<bot-id>].exists>
        // @returns ElementTag(Boolean)
        // @plugin dDiscordBot
        // @description
        // Returns whether a Discord bot exists with the given bot ID.
        // -->
        if (attribute.startsWith("exists")) {
            event.setReplacedObject(CoreUtilities.autoAttrib(new ElementTag(bot != null), attribute.fulfill(1)));
            return;
        }

        // <--[tag]
        // @attribute <discord[<bot-id>]>
        // @returns DiscordBotTag
        // @plugin dDiscordBot
        // @description
        // Returns the Discord bot for the given bot ID.
        // -->
        if (bot == null) {
            return;
        }
        event.setReplacedObject(CoreUtilities.autoAttrib(bot, attribute));
    }
}
