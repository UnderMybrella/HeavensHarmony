package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.ServerData
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

class RoleNamePermission(val roleName: String): OwnerPermission() {
    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean {
        if(super.hasPermission(user, channel, serverData))
            return true
        else
            return (channel.guild ?: return false).getRolesForUser(user).any { role -> role.name.equals(roleName, true) }
    }

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String = "This command is restricted to users with the $roleName role."
}