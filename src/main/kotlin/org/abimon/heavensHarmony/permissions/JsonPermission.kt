package org.abimon.heavensHarmony.permissions

import com.fasterxml.jackson.databind.JsonMappingException
import org.abimon.dArmada.ServerData
import org.abimon.heavensHarmony.getValueFor
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser

class JsonPermission(val required: String): OwnerPermission() {
    override fun getPermission(user: IUser, channel: IChannel, serverData: ServerData): EnumJsonState {
        if(channel.isPrivate)
            return EnumJsonState.GRANT
        else
            serverData.getPermissions(user, channel).forEach { perm -> perm.permissions.map { node -> node.match(required) }.forEach { matched -> if(matched != EnumJsonState.CONTINUE) return@getPermission matched } }

        return EnumJsonState.CONTINUE
    }

    override fun hasPermission(user: IUser, channel: IChannel, serverData: ServerData): Boolean {
        if(super.hasPermission(user, channel, serverData))
            return true
        else {
            val perm = getPermission(user, channel, serverData)
            when(perm) {
                EnumJsonState.GRANT -> return true
                EnumJsonState.CONTINUE -> return super.hasPermission(user, channel, serverData)
                EnumJsonState.DENY -> return false
            }
        }
    }

    override fun getPermissionDenied(user: IUser, channel: IChannel, serverData: ServerData): String = "This command is restricted to users with the `$required` permission."

    fun ServerData.getPermissions(user: IUser, channel: IChannel): List<JacksonPermissionObject> {
        if(channel.isPrivate)
            return listOf(JacksonPermissionObject("", null, null, null, null, null, arrayOf(JacksonPermissionNode(EnumJsonState.GRANT, "*"))))
        else {
            try {
                val roleIDs = user.getRolesForGuild(channel.guild).map { role -> role.longID }
                val roleNames = user.getRolesForGuild(channel.guild).map { role -> role.name.toLowerCase() }
                return (this.getValueFor("permissions", Array<JacksonPermissionObject>::class) ?: return listOf())
                        .filter { perm -> perm.channels?.any { channelID -> channel.longID == channelID } ?: true }
                        .filter { perm -> perm.role_ids?.any { roleID -> roleID in roleIDs } ?: true }
                        .filter { perm -> perm.user_ids?.any { userID -> user.longID == userID  } ?: true }
                        .filter { perm -> perm.role_names?.any { name -> name in roleNames } ?: true }
                        .filter { perm -> perm.user_names?.any { name -> name.equals(user.name, true) || name.equals(user.run { "$name#$discriminator" }, true) } ?: true }
            }
            catch(json: JsonMappingException) {
                println(json)
            }
        }

        return listOf()
    }
}