package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.ServerData
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

open class UserPermission(val id: Long): BrellaPermission() {
    var usr: IUser? = null

    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean = user.longID == id

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String {
        if(usr == null)
            usr = user.client.getUserByID(id)

        return "This command is restricted to ${usr?.run { "$name#$discriminator" } ?: "Unknown (ID: $id)"}."
    }
}