package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.ServerData
import org.abimon.heavensHarmony.HeavensBot
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

open class OwnerPermission: UserPermission(HeavensBot.INSTANCE_CONFIG?.ownerID ?: 149031328132628480L) {
    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean {
        if(channel.isPrivate)
            return true
        else
            return ((channel.guild ?: return super.hasPermission(user, channel, serverData)).owner.longID == user.longID) || super.hasPermission(user, channel, serverData)
    }

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String = "This command is restricted to the owner."
}