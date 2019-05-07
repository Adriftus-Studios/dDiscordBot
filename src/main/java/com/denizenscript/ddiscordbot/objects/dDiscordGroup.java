package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.ddiscordbot.DiscordConnection;
import com.denizenscript.ddiscordbot.dDiscordBot;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.util.Snowflake;
import net.aufdemrand.denizencore.objects.*;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

import java.util.HashMap;

public class dDiscordGroup implements dObject {

    @Fetchable("discordgroup")
    public static dDiscordGroup valueOf(String string, TagContext context) {
        if (string.startsWith("discordgroup@")) {
            string = string.substring("discordgroup@".length());
        }
        if (string.contains("@")) {
            return null;
        }
        int comma = string.indexOf(',');
        String bot = null;
        if (comma > 0) {
            bot = string.substring(0, comma);
            string = string.substring(comma + 1);
        }
        long grpId = aH.getLongFrom(string);
        if (grpId == 0) {
            return null;
        }
        return new dDiscordGroup(bot, grpId);
    }

    public static boolean matches(String arg) {
        if (arg.startsWith("discordgroup@")) {
            return true;
        }
        if (arg.contains("@")) {
            return false;
        }
        int comma = arg.indexOf(',');
        if (comma == -1) {
            return aH.matchesInteger(arg);
        }
        if (comma == arg.length() - 1) {
            return false;
        }
        return aH.matchesInteger(arg.substring(comma + 1));
    }

    public dDiscordGroup(String bot, long guildId) {
        this.bot = bot;
        this.guild_id = guildId;
        if (bot != null) {
            DiscordConnection conn = dDiscordBot.instance.connections.get(bot);
            if (conn != null) {
                conn.client.getGuildById(Snowflake.of(guild_id)).block();
            }
        }
    }

    public dDiscordGroup(String bot, Guild guild) {
        this.bot = bot;
        this.guild = guild;
        guild_id = guild.getId().asLong();
    }

    public Guild guild;

    public String bot;

    public long guild_id;

    public static void registerTags() {

        // <--[tag]
        // @attribute <discordgroup@group.name>
        // @returns Element
        // @description
        // Returns the name of the group.
        // -->
        registerTag("name", new TagRunnable() {
            @Override
            public String run(Attribute attribute, dObject object) {
                return new Element(((dDiscordGroup) object).guild.getName())
                        .getAttribute(attribute.fulfill(1));
            }
        });

        // <--[tag]
        // @attribute <discordgroup@group.id>
        // @returns Element(Number)
        // @description
        // Returns the ID number of the group.
        // -->
        registerTag("id", new TagRunnable() {
            @Override
            public String run(Attribute attribute, dObject object) {
                return new Element(((dDiscordGroup) object).guild.getId().asLong())
                        .getAttribute(attribute.fulfill(1));
            }
        });

        // <--[tag]
        // @attribute <discord@bot.channel[<name>]>
        // @returns DiscordChannel
        // @description
        // Returns the channel that best matches the input name, or null if there's no match.
        // -->
        registerTag("channel", new TagRunnable() {
            @Override
            public String run(Attribute attribute, dObject object) {
                if (!attribute.hasContext(1)) {
                    return null;
                }
                String matchString = CoreUtilities.toLowerCase(attribute.getContext(1));
                Channel bestMatch = null;
                for (GuildChannel chan : ((dDiscordGroup) object).guild.getChannels().toIterable()) {
                    String chanName = CoreUtilities.toLowerCase(chan.getName());
                    if (matchString.equals(chanName)) {
                        bestMatch = chan;
                        break;
                    }
                    if (chanName.contains(matchString)) {
                        bestMatch = chan;
                    }
                }
                if (bestMatch == null) {
                    return null;
                }
                return new dDiscordChannel(((dDiscordGroup) object).bot, bestMatch)
                        .getAttribute(attribute.fulfill(1));
            }
        });
    }

        public static HashMap<String, TagRunnable> registeredTags = new HashMap<>();

    public static void registerTag(String name, TagRunnable runnable) {
        if (runnable.name == null) {
            runnable.name = name;
        }
        registeredTags.put(name, runnable);
    }

    @Override
    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // TODO: Scrap getAttribute, make this functionality a core system
        String attrLow = CoreUtilities.toLowerCase(attribute.getAttributeWithoutContext(1));
        TagRunnable tr = registeredTags.get(attrLow);
        if (tr != null) {
            if (!tr.name.equals(attrLow)) {
                net.aufdemrand.denizencore.utilities.debugging.dB.echoError(attribute.getScriptEntry() != null ? attribute.getScriptEntry().getResidingQueue() : null,
                        "Using deprecated form of tag '" + tr.name + "': '" + attrLow + "'.");
            }
            return tr.run(attribute, this);
        }

        return new Element(identify()).getAttribute(attribute);
    }

    String prefix = "discordgroup";

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
        return "DiscordGroup";
    }

    @Override
    public String identify() {
        if (bot != null) {
            return "discordgroup@" + bot + "," + guild_id;
        }
        return "discordgroup@" + guild_id;
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
    public dObject setPrefix(String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        }
        return this;
    }
}