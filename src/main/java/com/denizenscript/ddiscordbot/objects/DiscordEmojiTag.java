package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.ddiscordbot.DenizenDiscordBot;
import com.denizenscript.ddiscordbot.DiscordConnection;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.*;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import discord4j.common.util.Snowflake;
import discord4j.core.object.reaction.ReactionEmoji;

public class DiscordEmojiTag implements ObjectTag {

    @Fetchable("discordemoji")
    public static DiscordEmojiTag valueOf(String string, TagContext context) {
        if (string.startsWith("discordemoji@")) {
            string = string.substring("discordemoji@".length());
        }
        if (string.contains("@")) {
            return null;
        }
        try {
            DiscordEmojiTag tag = null;
            String bot = string.split(",")[0];
            String type = string.split(",")[1];
            String id = string.split(",")[2];
            if (type.equalsIgnoreCase("unicode")) {
                tag = new DiscordEmojiTag(bot, ReactionEmoji.unicode(id));
            } else if (type.equalsIgnoreCase("custom")) {
                String name = string.split(",")[3];
                String animated = string.split(",")[4];
                tag = new DiscordEmojiTag(bot, ReactionEmoji.custom(Snowflake.of(Long.parseLong(id)), name, Boolean.valueOf(animated)));
            }
            return tag;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean matches(String arg) {
        System.out.println(arg);
        if (arg.startsWith("discordemoji@")) {
            return true;
        }
        try {
            String bot = arg.split(",")[0];
            String type = arg.split(",")[1];
            String id = arg.split(",")[2];
            if (bot != null && type != null && id != null) {
                return true;
            }
        } catch (Exception err) {}
        System.out.println("false");
        if (arg.contains("@")) {
            return false;
        }
        int comma = arg.indexOf(',');
        if (comma == -1) {
            return ArgumentHelper.matchesInteger(arg);
        }
        if (comma == arg.length() - 1) {
            return false;
        }
        return ArgumentHelper.matchesInteger(arg.substring(comma + 1));
    }

    public DiscordEmojiTag(String bot, ReactionEmoji emoji) {
        this.bot = bot;
        this.emoji = emoji;
    }

    public String getId() {
        if (emoji.asCustomEmoji().isPresent()) {
            emoji_id = emoji.asCustomEmoji().get().getId().asString();
        } else if (emoji.asUnicodeEmoji().isPresent()){
            emoji_id = emoji.asUnicodeEmoji().get().getRaw();
        } else {
            emoji_id = "INVALID";
        }
        return emoji_id;
    }

    public static DiscordConnection getBot() {
        return DenizenDiscordBot.instance.connections.get(bot);
    }

    public static String bot;

    public ReactionEmoji emoji;

    public String emoji_id;

    public static void registerTags() {

        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            public void run(ReplaceableTagEvent event) {
                if (event.matches("discordemoji") && !event.replaced()) {
                    DiscordEmojiTag tag = null;
                    if (event.hasNameContext()) {
                        String context = event.getNameContext();
                        tag = valueOf(context, event.getContext());
                    }
                    if (tag != null) {
                        event.setReplacedObject(CoreUtilities.autoAttrib(tag, event.getAttributes().fulfill(1)));
                    }
                }
            }
        }, "discordemoji");


        // <--[tag]
        // @attribute <DiscordEmojiTag.id>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the ID of the emoji.
        // -->
        registerTag("id", (attribute, object) -> {
            return new ElementTag(object.getId());
        });

        // <--[tag]
        // @attribute <DiscordEmojiTag.name>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the name of the emoji. (Unicode Emojis do not have a name)
        // -->
        registerTag("name", (attribute, object) -> {
            String emoji_id;
            if (object.emoji.asCustomEmoji().isPresent()) {
                emoji_id = object.emoji.asCustomEmoji().get().getName();
            } else if (object.emoji.asUnicodeEmoji().isPresent()){
                emoji_id = object.emoji.asUnicodeEmoji().get().getRaw();
            } else {
                emoji_id = "INVALID";
            }
            return new ElementTag(emoji_id);
        });

        // <--[tag]
        // @attribute <DiscordEmojiTag.animated>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns whether or not the emoji is animated.
        // -->
        registerTag("animated", (attribute, object) -> {
            boolean animated;
            if (object.emoji.asCustomEmoji().isPresent()) {
                animated = object.emoji.asCustomEmoji().get().isAnimated();
            } else {
                animated = false;
            }
            return new ElementTag(animated);
        });

        // <--[tag]
        // @attribute <DiscordEmojiTag.formatted>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the formatted emoji used in discord messages.
        // -->
        registerTag("formatted", (attribute, object) -> {
            if (object.emoji.asCustomEmoji().isPresent()) {
                return new ElementTag("<:" + object.emoji.asCustomEmoji().get().getName() + ":" + object.emoji.asCustomEmoji().get().getId().asLong() + ">");
            } else if (object.emoji.asUnicodeEmoji().isPresent()) {
                return new ElementTag(object.emoji.asUnicodeEmoji().get().getRaw());
            }
            return null;
        });
    }

    public static ObjectTagProcessor<DiscordEmojiTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTag(String name, TagRunnable.ObjectInterface<DiscordEmojiTag> runnable, String... variants) {
        tagProcessor.registerTag(name, runnable, variants);
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    String prefix = "discordemoji";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>'  ");
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String getObjectType() {
        return "DiscordEmoji";
    }

    @Override
    public String identify() {
        if (emoji.asCustomEmoji().isPresent()) {
            emoji_id = "custom," + emoji.asCustomEmoji().get().getId().asString() + "," + emoji.asCustomEmoji().get().getName() + "," + emoji.asCustomEmoji().get().isAnimated();
        } else if (emoji.asUnicodeEmoji().isPresent()){
            emoji_id = "unicode," + emoji.asUnicodeEmoji().get().getRaw();
        } else {
            emoji_id = "INVALID";
        }
        if (bot != null) {
            return "discordemoji@" + bot + "," + emoji_id;
        }
        return "discordemoji@" + emoji_id;
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        }
        return this;
    }
}
