package org.abimon.heavens_harmony

import discord4j.core.DiscordClient
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.util.Snowflake
import discord4j.core.event.domain.message.MessageEvent

object StateDatabase {
    val states = HashMap<Pair<Long, Long>, Pair<Any?, (Message, Any?) -> Unit>>()

    fun registerState(user: Snowflake, channel: Snowflake, state: Any?, callback: (Message, Any?) -> Unit) {
        states[user.asLong() to channel.asLong()] = state to callback
    }

    fun registerState(msg: Message, state: Any?, callback: (Message, Any?) -> Unit) {
        states[msg.authorId.map(Snowflake::asLong).orElse(0) to msg.channelId.asLong()] = state to callback
    }

    fun register(client: DiscordClient) {
        client.eventDispatcher.on(MessageEvent::class.java)
                .flatMapToMessage()
                .subscribe { msg ->
                    val (state, func) = states.remove(msg.authorId.map(Snowflake::asLong).orElse(0) to msg.channelId.asLong()) ?: return@subscribe
                    func(msg, state)
                }

    }
}