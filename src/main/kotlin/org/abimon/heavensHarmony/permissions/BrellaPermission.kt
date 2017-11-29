package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.ServerData
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

open class BrellaPermission: IHeavenPermission {
    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean = user.longID == 149031328132628480L

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String = "This command is restricted to Brella."
}