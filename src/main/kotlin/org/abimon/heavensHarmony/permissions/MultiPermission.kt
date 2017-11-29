package org.abimon.heavensHarmony.permissions

import org.abimon.dArmada.ServerData
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

class MultiPermission(val permissions: List<IHeavenPermission>, val operate: (EnumJsonState, EnumJsonState) -> EnumJsonState) : OwnerPermission() {
    companion object {
        val AND: (EnumJsonState, EnumJsonState) -> EnumJsonState = { one, two -> if(one == two) one else if(one == EnumJsonState.DENY || two == EnumJsonState.DENY) EnumJsonState.DENY else EnumJsonState.CONTINUE }
        val OR: (EnumJsonState, EnumJsonState) -> EnumJsonState = { one, two -> if(one == EnumJsonState.DENY || two == EnumJsonState.DENY) EnumJsonState.DENY else if(one == EnumJsonState.GRANT || two == EnumJsonState.GRANT) EnumJsonState.GRANT else EnumJsonState.CONTINUE }
        val XOR: (EnumJsonState, EnumJsonState) -> EnumJsonState = { one, two -> if(one == two) EnumJsonState.CONTINUE else if(one == EnumJsonState.DENY || two == EnumJsonState.DENY) EnumJsonState.DENY else if(one == EnumJsonState.GRANT || two == EnumJsonState.GRANT) EnumJsonState.GRANT else EnumJsonState.CONTINUE }

        val userPermission = EnumMemberStatus.USER.permission
        val verifiedPermission = EnumMemberStatus.VERIFIED.permission
        val moderatorPermission = EnumMemberStatus.MODERATOR.permission
        val adminPermission = EnumMemberStatus.ADMINISTRATOR.permission

        fun defaultOrJson(scope: String): MultiPermission = MultiPermission(listOf(JsonPermission(scope), userPermission), OR)
        fun verifiedOrJson(scope: String): MultiPermission = MultiPermission(listOf(JsonPermission(scope), verifiedPermission), OR)
        fun moderatorOrJson(scope: String): MultiPermission = MultiPermission(listOf(JsonPermission(scope), moderatorPermission), OR)
        fun adminOrJson(scope: String): MultiPermission = MultiPermission(listOf(JsonPermission(scope), adminPermission), OR)
    }

    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean {
        if(super.hasPermission(user, channel, serverData))
            return true

        val result = permissions.map { it.getPermission(user, channel, serverData) }.fold(EnumJsonState.CONTINUE, operate)
        if (result == EnumJsonState.GRANT)
            return true
        return false
    }

    override fun getPermission(user: IUser, channel: IChannel, serverData: ServerData): EnumJsonState = permissions.map { it.getPermission(user, channel, serverData) }.fold(EnumJsonState.CONTINUE, operate)

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String {
        return "This command is restricted by an amalgamation of rulings:\n\t* ${permissions.joinToString("\n\t* ") { it.getPermissionDenied(user, channel, serverData) }}"
    }
}