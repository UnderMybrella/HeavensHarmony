package org.abimon.heavensHarmony

import org.abimon.dArmada.DiscordCommand
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.PermissionUtils

object MenuDatabase {
    val menus = HashMap<Long, Map<String, MenuOperation>>()

    val reactionAdded = DiscordCommand<ReactionAddEvent> { event ->
        if(!event.user.isBot)
                return@DiscordCommand
        val operations = menus[event.message.longID] ?: return@DiscordCommand
        val operate = operations[event.reaction.emoji.toString()] ?: return@DiscordCommand
        operate(event.message, event.reaction, event.user)

        if(!event.channel.isPrivate && PermissionUtils.hasPermissions(event.channel, event.client.ourUser, Permissions.MANAGE_MESSAGES))
            event.message.removeReaction(event.user, event.reaction)
    }

    fun registerMenu(msg: IMessage, buttons: List<String>, operation: MenuOperation) = registerMenu(msg, buttons.map { it to operation })
    fun registerMenu(msg: IMessage, operations: List<Pair<String, MenuOperation>>) {
        menus.put(msg.longID, operations.toMap())
        operations.forEach { (reaction) -> msg.react(reaction) }
    }
    fun registerMenu(msg: IMessage, operations: Map<String, MenuOperation>) = menus.put(msg.longID, operations)
    fun removeMenu(msg: IMessage) = menus.remove(msg.longID)
}