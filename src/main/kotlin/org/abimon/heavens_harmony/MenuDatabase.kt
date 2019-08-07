package org.abimon.heavens_harmony

import discord4j.core.DiscordClient
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.event.domain.message.ReactionAddEvent
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

object MenuDatabase {
    val menus = HashMap<Pair<Long, Long>, Map<String, MenuOperation<*>>>()

    fun registerMenu(msg: Message, forUser: Long, buttons: List<String>, operation: MenuOperation<*>): Flux<Void> = registerMenu(msg, forUser, buttons.map { it to operation })
    fun registerMenu(msg: Message, forUser: Long, operations: List<Pair<String, MenuOperation<*>>>): Flux<Void> = registerMenu(msg, forUser, operations.toMap())
    fun registerMenu(msg: Message, forUser: Long, operations: Map<String, MenuOperation<*>>): Flux<Void> {
        menus[Pair(msg.id.asLong(), forUser)] = operations
        return Flux.fromArray(operations.keys.toTypedArray())
                .flatMap { reaction -> msg.addReaction(ReactionEmoji.unicode(reaction)) }
    }

    fun removeMenu(msg: Message) = menus.keys
            .filter { pair -> pair.first == msg.id.asLong() }
            .forEach { pair -> menus.remove(pair) }

    fun removeMenu(msg: Message, forUser: Long) = menus.remove(Pair(msg.id.asLong(), forUser))

    fun register(client: DiscordClient) {
        client.eventDispatcher.on(ReactionAddEvent::class.java)
                .filterWhen { event -> event.user.map { user -> !user.isBot } }
                .flatMap { event ->
                    event.user.flatMap { user ->
                        event.message.flatMap msg@{ msg ->
                            val operations = menus[Pair(msg.id.asLong(), user.id.asLong())] ?: return@msg Mono.empty<Void>()
                            val emojiID = event.emoji.asCustomEmoji().map { emoji -> emoji.id.asString() }.orElseGet { event.emoji.asUnicodeEmoji().map { unicode -> unicode.raw }.orElseGet(event.emoji::toString) }
                            val operate = operations[emojiID] ?: return@msg Mono.empty<Void>()
                            operate(msg, event.emoji, user).then(msg.removeReaction(event.emoji, user.id))
                        }
                    }
                }
                .onErrorContinue(::emptyErrorContinue)
                .subscribe()
    }
}