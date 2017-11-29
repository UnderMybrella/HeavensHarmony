package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.ServerData
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.IUser

class RoleIDPermission(val roleID: Long): OwnerPermission() {
    var role: IRole? = null

    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean {
        if(super.hasPermission(user, channel, serverData))
            return true
        else
            return (channel.guild ?: return false).getRolesForUser(user).any { role -> role.longID == roleID }
    }

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String {
        if(role == null)
            role = user.client.getRoleByID(roleID)

        return "This command is restricted to users with the ${role?.name ?: "Unknown (ID: $roleID)"} role."
    }
}