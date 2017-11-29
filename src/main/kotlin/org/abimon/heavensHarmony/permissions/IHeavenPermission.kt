package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.MessageOrder
import org.abimon.dArmada.ServerData
import org.abimon.dArmada.ServerMessageOrder
import org.abimon.dArmada.serverData
import org.abimon.heavensHarmony.buffer
import org.abimon.imperator.handle.Order
import org.abimon.imperator.handle.Watchtower
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder

interface IHeavenPermission: Watchtower {
    fun getPermission(user: IUser, channel: IChannel, serverData: ServerData): EnumJsonState = if(hasPermission(user, channel, serverData)) EnumJsonState.GRANT else EnumJsonState.DENY
    fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean
    fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String
    fun onPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData) {
        buffer { channel.sendMessage(EmbedBuilder().withTitle("Missing Permissions").withDesc(getPermissionDenied(user, channel, serverData)).build()) }
    }

    override fun allow(order: Order): Boolean {
        if(order is MessageOrder) {
            if (order is ServerMessageOrder)
                return hasPermission(order.author, order.channel, order.server.serverData)
            else
                return true
        }

        return false
    }
    override fun getName(): String = "Miku Permission $this"
}