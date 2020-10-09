package com.denizenscript.ddiscordbot;

import com.denizenscript.ddiscordbot.objects.*;
import com.denizenscript.denizencore.objects.Argument;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.Holdable;
import com.denizenscript.denizencore.scripts.queues.ScriptQueue;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import discord4j.discordjson.json.ActivityUpdateRequest;
import discord4j.discordjson.json.EmbedFieldData;
import discord4j.discordjson.json.ImmutableEmbedData;
import discord4j.discordjson.json.gateway.StatusUpdate;
import discord4j.common.util.Snowflake;
import discord4j.store.jdk.JdkStoreService;
import org.bukkit.Bukkit;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.Function;

public class DiscordCommand extends AbstractCommand implements Holdable {

    public DiscordCommand() {
        setName("discord");
        setSyntax("discord [id:<id>] [connect code:<botcode>/disconnect/message/add_role/start_typing/stop_typing/remove_role/status (status:<status>) (activity:<activity>)/rename/edit_message/delete_message] (<message>) (message_id:<id>) (channel:<channel>) (user:<user>) (group:<group>) (role:<role>) (url:<url>)");
        setRequiredArguments(2, 12);
    }

    // <--[command]
    // @Name discord
    // @Syntax discord [id:<id>] [connect code:<botcode>/disconnect/message/add_role/start_typing/stop_typing/remove_role/status (status:<status>) (activity:<activity>)/rename/edit_message/delete_message] (<message>) (message_id:<id>) (channel:<channel>) (user:<user>) (group:<group>) (role:<role>) (url:<url>)
    // @Required 2
    // @Maximum 12
    // @Short Connects to and interacts with Discord.
    // @Plugin dDiscordBot
    // @Group external
    //
    // @Description
    // Connects to and interacts with Discord.
    //
    // Commands may fail if the bot does not have permission within the Discord group to perform them.
    //
    // When setting the status of the Discord bot, the status argument can be: ONLINE, DND, IDLE, or INVISIBLE,
    // and the activity argument can be: PLAYING, STREAMING, LISTENING, or WATCHING.
    // Streaming activity requires a 'url:' input.
    //
    // The command should always be ~waited for. See <@link language ~waitable>.
    //
    // Do not type your bot token code directly into the script.
    // The generally recommend way to track tokens is in a separate data YAML file.
    // Load the file, issue the connect command, then unload the file. Do not keep it in server memory any longer than needed.
    //
    // @Tags
    // <discord[<bot_id>]>
    // <entry[saveName].message_id> returns the ID of the sent message, when the command is ~waited for, and the 'message' argument is used.
    //
    // @Usage
    // Use to connect to Discord via a bot code.
    // - ~discord id:mybot connect code:<[code]>
    //
    // @Usage
    // Use to disconnect from Discord.
    // - ~discord id:mybot disconnect
    //
    // @Usage
    // Use to message a Discord channel.
    // - ~discord id:mybot message channel:<discord[mybot].group[Denizen].channel[bot-spam]> "Hello world!"
    //
    // @Usage
    // Use to message a Discord channel and record the ID.
    // - ~discord id:mybot message channel:<discord[mybot].group[Denizen].channel[bot-spam]> "Hello world!" save:sent
    // - announce "Sent as <entry[sent].message_id>"
    //
    // @Usage
    // Use to send a message to a user through a private channel.
    // - ~discord id:mybot message user:<[user]> "Hello world!"
    //
    // @Usage
    // Use to add a role on a user in a Discord guild.
    // - ~discord id:mybot add_role user:<[user]> role:<[role]> group:<[group]>
    //
    // @Usage
    // Use to remove a role on a user in a Discord guild.
    // - ~discord id:mybot remove_role user:<[user]> role:<[role]> group:<[group]>
    //
    // @Usage
    // Use to set the online status of the bot, and clear the game status.
    // - ~discord id:mybot status "Minecraft" "status:ONLINE"
    //
    // @Usage
    // Use to set the game status of the bot.
    // - ~discord id:mybot status "Minecraft" "status:ONLINE" "activity:PLAYING"
    //
    // @Usage
    // Use to change the bot's nickname.
    // - ~discord id:mybot rename "<[nickname]>" group:<[group]>
    //
    // @Usage
    // Use to give a user a new nickname.
    // - ~discord id:mybot rename "<[nickname]>" user:<[user]> group:<[group]>
    //
    // @Usage
    // Use to start typing in a specific channel.
    // - ~discord id:mybot start_typing channel:<[channel]>
    //
    // @Usage
    // Use to stop typing in a specific channel.
    // - ~discord id:mybot stop_typing channel:<[channel]>
    //
    // @Usage
    // Use to edit a message the bot has already sent.
    // - ~discord id:mybot edit_message channel:<[channel]> message_id:<[msg]> "Wow! It got edited!"
    //
    // @Usage
    // Use to delete a message the bot has already sent.
    // - ~discord id:mybot delete_message channel:<[channel]> message_id:<[msg]>
    //
    // @Usage
    // Use to send embedded messages.
    // - ~discord id:mybot send_embed channel:<[channel]> embed:<discordembed.title[test].description[something]>
    //
    // @Usage
    // Use to send embedded messages to a user.
    // - ~discord id:mybot send_embed user:<[user]> embed:<discordembed.title[test].description[something]>
    //
    // -->

    public enum DiscordInstruction { CONNECT, DISCONNECT, MESSAGE, ADD_ROLE, REMOVE_ROLE, STATUS, RENAME, START_TYPING, STOP_TYPING, EDIT_MESSAGE, DELETE_MESSAGE, SEND_EMBED }

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (Argument arg : scriptEntry.getProcessedArgs()) {
            if (!scriptEntry.hasObject("id")
                    && arg.matchesPrefix("id")) {
                scriptEntry.addObject("id", new ElementTag(CoreUtilities.toLowerCase(arg.getValue())));
            }
            else if (!scriptEntry.hasObject("instruction")
                    && arg.matchesEnum(DiscordInstruction.values())) {
                scriptEntry.addObject("instruction", arg.asElement());
            }
            else if (!scriptEntry.hasObject("code")
                    && arg.matchesPrefix("code")) {
                scriptEntry.addObject("code", arg.asElement());
            }
            else if (!scriptEntry.hasObject("embed")
                    && arg.matchesPrefix("embed")
                    && arg.matchesArgumentType(DiscordEmbedTag.class)) {
                scriptEntry.addObject("embed", arg.asType(DiscordEmbedTag.class));
            }
            else if (!scriptEntry.hasObject("channel")
                    && arg.matchesPrefix("channel")
                    && arg.matchesArgumentType(DiscordChannelTag.class)) {
                scriptEntry.addObject("channel", arg.object);
            }
            else if (!scriptEntry.hasObject("url")
                    && arg.matchesPrefix("url")) {
                scriptEntry.addObject("url", arg.asElement());
            }
            else if (!scriptEntry.hasObject("user")
                    && arg.matchesPrefix("user")
                    && arg.matchesArgumentType(DiscordUserTag.class)) {
                scriptEntry.addObject("user", arg.asType(DiscordUserTag.class));
            }
            else if (!scriptEntry.hasObject("group")
                    && arg.matchesPrefix("group")
                    && arg.matchesArgumentType(DiscordGroupTag.class)) {
                scriptEntry.addObject("group", arg.asType(DiscordGroupTag.class));
            }
            else if (!scriptEntry.hasObject("role")
                    && arg.matchesPrefix("role")
                    && arg.matchesArgumentType(DiscordRoleTag.class)) {
                scriptEntry.addObject("role", arg.asType(DiscordRoleTag.class));
            }
            else if (!scriptEntry.hasObject("status")
                    && arg.matchesPrefix("status")) {
                scriptEntry.addObject("status", arg.asElement());
            }
            else if (!scriptEntry.hasObject("activity")
                    && arg.matchesPrefix("activity")) {
                scriptEntry.addObject("activity", arg.asElement());
            }
            else if (!scriptEntry.hasObject("message_id")
                    && arg.matchesPrefix("message_id")) {
                scriptEntry.addObject("message_id", arg.asElement());
            }
            else if (!scriptEntry.hasObject("message")) {
                scriptEntry.addObject("message", new ElementTag(arg.raw_value));
            }
            else if (!scriptEntry.hasObject("embed")
                    && arg.matchesArgumentType(DiscordEmbedTag.class)) {
                scriptEntry.addObject("embed", arg.asType(DiscordEmbedTag.class));
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("id")) {
            throw new InvalidArgumentsException("Must have an ID!");
        }
        if (!scriptEntry.hasObject("instruction")) {
            throw new InvalidArgumentsException("Must have an instruction!");
        }
    }

    public static class DiscordConnectThread extends Thread {

        public String code;

        public DiscordConnection conn;

        public Runnable ender;

        @Override
        public void run() {
            try {
                DiscordClient client = DiscordClientBuilder.create(code).build();
                conn.client = client.gateway().setStoreService(new JdkStoreService()).login().block();
                conn.registerHandlers();
            }
            catch (Exception ex) {
                Bukkit.getScheduler().runTask(DenizenDiscordBot.instance, () -> {
                    DenizenDiscordBot.instance.connections.remove(conn.botID);
                });
                Debug.echoError(ex);
            }
            Bukkit.getScheduler().runTask(DenizenDiscordBot.instance, ender);
        }
    }

    public static void errorMessage(ScriptQueue queue, String message) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(DenizenDiscordBot.instance, new Runnable() {
            @Override
            public void run() {
                Debug.echoError(queue, message);
            }
        }, 0);
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        ElementTag id = scriptEntry.getElement("id");
        ElementTag instruction = scriptEntry.getElement("instruction");
        ElementTag code = scriptEntry.getElement("code"); // Intentionally do not debug this value.
        DiscordChannelTag channel = scriptEntry.hasObject("channel") ? (scriptEntry.getObjectTag("channel") instanceof DiscordChannelTag ? scriptEntry.getObjectTag("channel") : new DiscordChannelTag(id.asString(), scriptEntry.getElement("channel").asLong())) : null;
        ElementTag message = scriptEntry.getElement("message");
        ElementTag status = scriptEntry.getElement("status");
        ElementTag activity = scriptEntry.getElement("activity");
        DiscordUserTag user = scriptEntry.getObjectTag("user");
        DiscordGroupTag guild = scriptEntry.getObjectTag("group");
        DiscordRoleTag role = scriptEntry.getObjectTag("role");
        ElementTag url = scriptEntry.getElement("url");
        ElementTag messageId = scriptEntry.getElement("message_id");

        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(), id.debug()
                    + (channel != null ? channel.debug() : "")
                    + instruction.debug()
                    + (message != null ? message.debug() : "")
                    + (user != null ? user.debug() : "")
                    + (guild != null ? guild.debug() : "")
                    + (role != null ? role.debug() : "")
                    + (status != null ? status.debug() : "")
                    + (activity != null ? activity.debug() : "")
                    + (url != null ? url.debug() : "")
                    + (messageId != null ? messageId.debug() : ""));
        }

        Supplier<Boolean> requireClientID = () -> {
            if (!DenizenDiscordBot.instance.connections.containsKey(id.asString())) {
                Debug.echoError(scriptEntry.getResidingQueue(), "Failed to process Discord " + instruction.asString() + " command: unknown ID!");
                scriptEntry.setFinished(true);
                return true;
            }
            return false;
        };
        Function<GatewayDiscordClient, Boolean> requireClientObject = (_client) -> {
            if (_client == null) {
                Debug.echoError(scriptEntry.getResidingQueue(), "The Discord bot '" + id.asString() + "'is not yet loaded.");
                scriptEntry.setFinished(true);
                return true;
            }
            return false;
        };
        BiFunction<Object, String, Boolean> requireObject = (obj, name) -> {
            if (obj == null) {
                Debug.echoError(scriptEntry.getResidingQueue(), "Failed to process Discord " + instruction.asString() + " command: no " + name + " given!");
                scriptEntry.setFinished(true);
                return true;
            }
            return false;
        };
        Supplier<Boolean> requireUser = () -> requireObject.apply(user, "user");
        Supplier<Boolean> requireChannel = () -> requireObject.apply(channel, "channel");
        Supplier<Boolean> requireMessage = () -> requireObject.apply(message, "message");
        Supplier<Boolean> requireGuild = () -> requireObject.apply(guild, "guild");
        Supplier<Boolean> requireRole = () -> requireObject.apply(role, "role");
        Supplier<Boolean> requireMessageId = () -> requireObject.apply(messageId, "message_id");
        DiscordInstruction instructionEnum = DiscordInstruction.valueOf(instruction.asString().toUpperCase());
        if (instructionEnum == DiscordInstruction.CONNECT) {
            if (requireObject.apply(code, "code")) {
                return;
            }
            if (DenizenDiscordBot.instance.connections.containsKey(id.asString())) {
                Debug.echoError(scriptEntry.getResidingQueue(), "Failed to connect: duplicate ID!");
                return;
            }
            DiscordConnection dc = new DiscordConnection();
            dc.botID = id.asString();
            DenizenDiscordBot.instance.connections.put(id.asString(), dc);
            DiscordConnectThread dct = new DiscordConnectThread();
            dct.code = code.asString();
            dct.conn = dc;
            dct.ender = () -> scriptEntry.setFinished(true);
            dct.start();
            return;
        }
        Runnable executeCore = () -> {
            switch (instructionEnum) {
                case DISCONNECT: {
                    if (requireClientID.get()) {
                        return;
                    }
                    DenizenDiscordBot.instance.connections.remove(id.asString()).client.logout().block();
                    scriptEntry.setFinished(true);
                    break;
                }
                case SEND_EMBED: {
                    DiscordEmbedTag embed = scriptEntry.getObjectTag("embed");
                    if (channel == null && user == null) {
                        if (!requireChannel.get()) {
                            requireUser.get();
                        }
                        scriptEntry.setFinished(true);
                        return;
                    }
                    if (requireClientID.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    if (embed != null) {
                        if (channel == null) {
                            PrivateChannel c = user.getUser().getPrivateChannel().block();
                            Message m = c.createEmbed(e -> {
                                try {
                                    Field f = e.getClass().getDeclaredField("requestBuilder");
                                    f.setAccessible(true);
                                    f.set(e, embed.build());
                                    for(EmbedFieldData field : embed.fields) {
                                        e.addField(field.name(), field.value(), field.inline().get());
                                    }
                                } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
                                    noSuchFieldException.printStackTrace();
                                }
                            }).block();
                            scriptEntry.addObject("message_id", new ElementTag(m.getId().asString()));
                            scriptEntry.addObject("message", new DiscordMessageTag(id.asString(), m));
                            scriptEntry.setFinished(true);
                        } else {
                            TextChannel c = (TextChannel) client.getChannelById(Snowflake.of(channel.channel_id)).block();
                            Message m = c.createEmbed(e -> {
                                try {
                                    Field f = e.getClass().getDeclaredField("requestBuilder");
                                    f.setAccessible(true);
                                    f.set(e, embed.build());
                                    for(EmbedFieldData field : embed.fields) {
                                        e.addField(field.name(), field.value(), field.inline().get());
                                    }
                                } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
                                    noSuchFieldException.printStackTrace();
                                }
                            }).block();
                            scriptEntry.addObject("message_id", new ElementTag(m.getId().asString()));
                            scriptEntry.addObject("message", new DiscordMessageTag(id.asString(), m));
                            scriptEntry.setFinished(true);
                        }
                    }
                    break;
                }
                case MESSAGE: {
                    if (channel == null && user == null) {
                        if (!requireChannel.get()) {
                            requireUser.get();
                        }
                        scriptEntry.setFinished(true);
                        return;
                    }
                    if (requireClientID.get() || requireMessage.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    if (channel == null) {
                        client.getUserById(Snowflake.of(user.user_id)).map(User::getPrivateChannel).flatMap(chanBork -> chanBork.flatMap(
                                chan -> chan.createMessage(message.asString())))
                                .map(m -> {
                                    scriptEntry.addObject("message_id", new ElementTag(m.getId().asString()));
                                    scriptEntry.addObject("message", new DiscordMessageTag(id.asString(), m));
                                    scriptEntry.setFinished(true);
                                    return m;
                                })
                                .doOnError(Debug::echoError).subscribe();
                    }
                    else {
                        if (message != null) {
                            client.getChannelById(Snowflake.of(channel.channel_id))
                                    .flatMap(chan -> ((TextChannel) chan).createMessage(message.asString()))
                                    .map(m -> {

                                        scriptEntry.addObject("message_id", new ElementTag(m.getId().asString()));
                                        scriptEntry.addObject("message", new DiscordMessageTag(id.asString(), m));
                                        scriptEntry.setFinished(true);
                                        return m;
                                    })
                                    .doOnError(Debug::echoError).subscribe();
                        }
                    }
                    break;
                }
                case ADD_ROLE: {
                    if (requireClientID.get() || requireUser.get() || requireGuild.get() || requireRole.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    client.getGuildById(Snowflake.of(guild.guild_id)).map(guildObj -> guildObj.getMemberById(Snowflake.of(user.user_id)))
                            .flatMap(memberBork -> memberBork.flatMap(member -> member.addRole(Snowflake.of(role.role_id))))
                            .doOnError(Debug::echoError).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
                case REMOVE_ROLE: {
                    if (requireClientID.get() || requireUser.get() || requireRole.get() || requireGuild.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    client.getGuildById(Snowflake.of(guild.guild_id)).map(guildObj -> guildObj.getMemberById(Snowflake.of(user.user_id)))
                            .flatMap(memberBork -> memberBork.flatMap(member -> member.removeRole(Snowflake.of(role.role_id))))
                            .doOnError(Debug::echoError).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
                case EDIT_MESSAGE: {
                    if (requireClientID.get() || requireChannel.get() || requireMessage.get() || requireMessageId.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    Message mes = client.getMessageById(Snowflake.of(channel.channel_id), Snowflake.of(messageId.asLong())).block();
                    if (mes == null) {
                        scriptEntry.setFinished(true);
                        // Not an error as this could happen for reasons the script isn't able to account for.
                        Debug.echoDebug(scriptEntry, "Message '" + messageId + "' does not exist.");
                        return;
                    }
                    mes.edit(m -> m.setContent(message.asString())).doOnError(Debug::echoError).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
                case DELETE_MESSAGE: {
                    if (requireClientID.get() || requireChannel.get() || requireMessageId.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    Message mes = client.getMessageById(Snowflake.of(channel.channel_id), Snowflake.of(messageId.asLong())).block();
                    if (mes == null) {
                        // Not an error as this could happen for reasons the script isn't able to account for.
                        Debug.echoDebug(scriptEntry, "Message '" + messageId + "' does not exist.");
                        scriptEntry.setFinished(true);
                        return;
                    }
                    mes.delete().doOnError(Debug::echoError).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
                case START_TYPING: {
                    if (requireClientID.get() || requireChannel.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    client.getChannelById(Snowflake.of(channel.channel_id))
                            .flatMap(chan -> ((TextChannel) chan).type())
                            .doOnError(Debug::echoError).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
                case STOP_TYPING: {
                    if (requireClientID.get() || requireChannel.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    client.getChannelById(Snowflake.of(channel.channel_id))
                            .map(chan -> ((TextChannel) chan).typeUntil(Mono.empty()))
                            .doOnError(Debug::echoError).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
                case RENAME: {
                    if (requireClientID.get() || requireGuild.get() || requireMessage.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    long userId;
                    if (user == null) {
                        userId = client.getSelfId().asLong();
                    }
                    else {
                        userId = user.user_id;
                    }
                    client.getGuildById(Snowflake.of(guild.guild_id)).map(guildObj -> guildObj.getMemberById(Snowflake.of(userId)))
                            .flatMap(memberBork -> memberBork.flatMap(member -> member.edit(spec -> spec.setNickname(message.asString()))))
                            .doOnError(Debug::echoError).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
                case STATUS: {
                    if (requireClientID.get()) {
                        return;
                    }
                    GatewayDiscordClient client = DenizenDiscordBot.instance.connections.get(id.asString()).client;
                    if (requireClientObject.apply(client)) {
                        return;
                    }
                    Activity.Type at = activity == null ? Activity.Type.PLAYING : Activity.Type.valueOf(activity.asString().toUpperCase());
                    ActivityUpdateRequest activityObject;
                    if (at == Activity.Type.WATCHING) {
                        activityObject = Activity.watching(message.asString());
                    }
                    else if (at == Activity.Type.STREAMING) {
                        activityObject = Activity.streaming(message.asString(), url.asString());
                    }
                    else if (at == Activity.Type.LISTENING) {
                        activityObject = Activity.listening(message.asString());
                    }
                    else {
                        activityObject = Activity.playing(message.asString());
                    }
                    String statusLower = status == null ? "online" : CoreUtilities.toLowerCase(status.asString());
                    StatusUpdate presence;
                    if (statusLower.equals("idle")) {
                        presence = Presence.idle(activityObject);
                    }
                    else if (statusLower.equals("dnd")) {
                        presence = Presence.doNotDisturb(activityObject);
                    }
                    else if (statusLower.equals("invisible")) {
                        presence = Presence.invisible();
                    }
                    else {
                        presence = Presence.online(activityObject);
                    }
                    client.updatePresence(presence).subscribe();
                    scriptEntry.setFinished(true);
                    break;
                }
            }
        };
        if (scriptEntry.shouldWaitFor()) {
            Bukkit.getScheduler().runTaskAsynchronously(DenizenDiscordBot.instance, executeCore);
        }
        else {
            executeCore.run();
        }
    }
}
