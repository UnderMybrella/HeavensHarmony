package org.abimon.heavensHarmony

import org.abimon.dArmada.MessageOrder
import org.abimon.imperator.impl.InstanceSoldier
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

object StateDatabase {
    val states = HashMap<Pair<Long, Long>, Pair<Any?, (MessageOrder, Any?) -> Unit>>()

    val messageSent = InstanceSoldier<MessageOrder>("State Catcher", emptyList()) { messageOrder ->
        val (state, func) = states.remove(messageOrder.author.longID to messageOrder.channel.longID) ?: return@InstanceSoldier
        func(messageOrder, state)
    }

    fun registerState(user: IUser, channel: IChannel, state: Any?, callback: (MessageOrder, Any?) -> Unit) {
        states[user.longID to channel.longID] = state to callback
    }

    fun registerState(order: MessageOrder, state: Any?, callback: (MessageOrder, Any?) -> Unit) {
        states[order.author.longID to order.channel.longID] = state to callback
    }
}