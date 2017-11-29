package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.ServerData
import org.abimon.heavensHarmony.getMemberStatuses
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

class MemberStatusPermission(val status: EnumMemberStatus): OwnerPermission() {
    override fun getPermission(user: IUser, channel: IChannel, serverData: ServerData): EnumJsonState = if(hasPermission(user, channel, serverData)) EnumJsonState.GRANT else EnumJsonState.CONTINUE
    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean {
        if(super.hasPermission(user, channel, serverData))
            return true
        else
            return user.getMemberStatuses(channel.guild).contains(status)
    }

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String = "This command is restricted to users with the $status status in this server."
}