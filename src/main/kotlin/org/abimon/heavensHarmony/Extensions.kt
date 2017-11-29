package org.abimon.heavensHarmony

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import org.abimon.dArmada.ServerData
import org.abimon.dArmada.serverData
import org.abimon.heavensHarmony.permissions.EnumMemberStatus
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer
import java.util.*
import kotlin.reflect.KClass

fun <T> buffer(action: () -> T): RequestBuffer.RequestFuture<T> = RequestBuffer.request(action)
fun <T> bufferAndWait(action: () -> T): T = RequestBuffer.request(action).get()

val IGuild.config: GuildConfig?
    get() = serverData.getValueFor("config", GuildConfig::class)

val IGuild.aliasPrefix: String
    get() = this.config?.prefix ?: HeavensBot.INSTANCE_CONFIG?.defaultPrefix ?: "~|"

fun IGuild.getAliasPaths(command: String): Array<Map<String, Any?>> {
    val commandAliases = (this.config?.command_aliases ?: emptyMap())[command] ?: return emptyArray()

    when (commandAliases) {
        is String -> return arrayOf(mapOf("alias" to commandAliases))
        is Map<*, *> -> return arrayOf(commandAliases.mapKeys { (key) -> key as? String ?: "" })
        is Array<*> -> return commandAliases.flatMap { alias ->
            when (alias) {
                is String -> return@flatMap listOf(mapOf("alias" to alias))
                is Map<*, *> -> return@flatMap listOf(alias.mapKeys { (key) -> key as? String ?: "" })
                is Array<*> -> return@flatMap alias.map { element -> mapOf("alias" to "$element") }
                else -> return@flatMap listOf(mapOf("alias" to alias.toString()))
            }
        }.toTypedArray()
        else -> return arrayOf(mapOf("alias" to commandAliases.toString()))
    }
}

//fun IGuild.getAliasPaths(command: String): Array<JSONObject> {
//    if (serverData["alias.json"] != null) {
//        try {
//            val aliasJson = serverData["alias.json"]!!.data.asJSONObject()
//            val aliasCommand = aliasJson["command [$command]"]
//            if (aliasCommand is String)
//                return arrayOf(JSONObject().put("alias", aliasCommand))
//            else if (aliasCommand is JSONObject)
//                return arrayOf(aliasCommand)
//            else if (aliasCommand is JSONArray)
//                return aliasCommand.toList().filter { it is String || it is JSONObject }.map { if (it is String) JSONObject().put("alias", it) else it as JSONObject }.toList().toTypedArray()
//        } catch(json: JSONException) {
//            return arrayOf(JSONObject().put("alias", "%prefix$command"))
//        }
//    }
//
//    return arrayOf(JSONObject().put("alias", "%prefix$command"))
//}

fun <T : Any> ServerData.getValueFor(name: String, klass: KClass<T>): T? {
    val json = this["$name.json"]
    if (json != null) {
        try {
            return HeavensBot.MAPPER.readValue(json.data, klass.java)
        } catch (json: JsonParseException) {
        } catch (json: JsonMappingException) {
        }
    }

    val yaml = this["$name.yaml"]
    if (yaml != null) {
        try {
            return HeavensBot.YAML_MAPPER.readValue(yaml.data, klass.java)
        } catch (json: JsonParseException) {
        } catch (json: JsonMappingException) {
        }
    }

    return null
}

fun IUser.getMemberStatuses(server: IGuild): EnumSet<EnumMemberStatus> {
    val enums = if (server.ownerLongID == longID) return EnumSet.allOf(EnumMemberStatus::class.java) else EnumSet.of(EnumMemberStatus.USER, EnumMemberStatus.VERIFIED)
    val serverSettings = server.config

    serverSettings?.admin_role?.also { roleID ->
        if (server.getRolesForUser(this).any { role -> role.longID == roleID })
            enums.add(EnumMemberStatus.ADMINISTRATOR)
    }

    serverSettings?.mod_role?.also { roleID ->
        if (server.getRolesForUser(this).any { role -> role.longID == roleID })
            enums.add(EnumMemberStatus.MODERATOR)
    }

    if (serverSettings?.verified_role != null) {
        enums.remove(EnumMemberStatus.VERIFIED)
        if (server.getRolesForUser(this).any { role -> role.longID == serverSettings.verified_role })
            enums.add(EnumMemberStatus.VERIFIED)
    }

    return enums
}