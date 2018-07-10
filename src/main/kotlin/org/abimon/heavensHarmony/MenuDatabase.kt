package org.abimon.heavensHarmony

import discord4j.core.DiscordClient
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.event.domain.message.ReactionAddEvent

object MenuDatabase {
    val menus = HashMap<Long, Map<String, MenuOperation>>()

    fun registerMenu(msg: Message, buttons: List<String>, operation: MenuOperation) = registerMenu(msg, buttons.map { it to operation })
    fun registerMenu(msg: Message, operations: List<Pair<String, MenuOperation>>) {
        menus.put(msg.id.asLong(), operations.toMap())
        operations.forEach { (reaction) -> msg.addReaction(ReactionEmoji.unicode(reaction)) }
    }

    fun registerMenu(msg: Message, operations: Map<String, MenuOperation>) = menus.put(msg.id.asLong(), operations)
    fun removeMenu(msg: Message) = menus.remove(msg.id.asLong())

    fun register(client: DiscordClient) {
        client.eventDispatcher.on(ReactionAddEvent::class.java)
                .subscribe { event ->
                    event.user.subscribe { user ->
                        event.message.subscribe msg@{ msg ->
                            val operations = menus[msg.id.asLong()] ?: return@msg
                            val emojiID = event.emoji.asCustomEmoji().map { emoji -> emoji.id.asString() }.orElseGet { event.emoji.asUnicodeEmoji().map { unicode -> unicode.raw }.orElseGet(event.emoji::toString) }
                            val operate = operations[emojiID] ?: return@msg
                            operate(msg, event.emoji, user)

                            msg.removeReaction(event.emoji, user.id)
                        }
                    }
                }
    }
}