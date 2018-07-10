package org.abimon.heavensHarmony

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

//Type Aliases

typealias MenuOperation = (Message, ReactionEmoji, User) -> Unit

//General Methods

fun Flux<MessageEvent>.flatMapToMessage(): Flux<Message> =
        filter { event ->
            event is MessageCreateEvent || event is MessageUpdateEvent
        }.flatMap { event ->
            when (event) {
                is MessageCreateEvent -> event.message.toMono()
                is MessageUpdateEvent -> event.message.toMono()
                else -> Mono.empty<Message>()
            }
        }


//IGuild

//var Guild.config: GuildConfig?
//    get() = serverData.getValueFor("config", GuildConfig::class)
//    set(value) { serverData.setValueFor("config", value) }
//
//val IGuild.aliasPrefix: String
//    get() = this.config?.prefix ?: HeavensBot.INSTANCE_CONFIG?.defaultPrefix ?: "~|"
//
//fun IGuild.getAliasPaths(command: String): Array<Map<String, Any?>> {
//    val commandAliases = (this.config?.command_aliases ?: emptyMap())[command] ?: return emptyArray()
//
//    when (commandAliases) {
//        is String -> return arrayOf(mapOf("alias" to commandAliases))
//        is Map<*, *> -> return arrayOf(commandAliases.mapKeys { (key) -> key as? String ?: "" })
//        is Array<*> -> return commandAliases.flatMap { alias ->
//            when (alias) {
//                is String -> return@flatMap listOf(mapOf("alias" to alias))
//                is Map<*, *> -> return@flatMap listOf(alias.mapKeys { (key) -> key as? String ?: "" })
//                is Array<*> -> return@flatMap alias.map { element -> mapOf("alias" to "$element") }
//                else -> return@flatMap listOf(mapOf("alias" to alias.toString()))
//            }
//        }.toTypedArray()
//        else -> return arrayOf(mapOf("alias" to commandAliases.toString()))
//    }
//}

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

//ServerData

//fun <T : Any> ServerData.getValueFor(name: String, klass: KClass<T>): T? {
//    val json = this["$name.json"]
//    if (json != null) {
//        try {
//            return HeavensBot.MAPPER.readValue(json.data, klass.java)
//        } catch (json: JsonParseException) {
//        } catch (json: JsonMappingException) {
//        }
//    }
//
//    val yaml = this["$name.yaml"]
//    if (yaml != null) {
//        try {
//            return HeavensBot.YAML_MAPPER.readValue(yaml.data, klass.java)
//        } catch (json: JsonParseException) {
//        } catch (json: JsonMappingException) {
//        }
//    }
//
//    return null
//}
//
//inline fun <reified T : Any> ServerData.setValueFor(name: String, t: T?): T? {
//    val json = this["$name.json"]
//    if (json != null) {
//        try {
//            val oldValue = HeavensBot.MAPPER.readValue(json.data, T::class.java)
//            this["$name.json"] = HeavensBot.MAPPER.writeValueAsBytes(t)
//
//            return oldValue
//        } catch (json: JsonParseException) {
//        } catch (json: JsonMappingException) {
//        }
//    }
//
//    val yaml = this["$name.yaml"]
//    var oldValue: T? = null
//    if (yaml != null) {
//        try {
//            oldValue = HeavensBot.YAML_MAPPER.readValue(yaml.data, T::class.java)
//        } catch (json: JsonParseException) {
//        } catch (json: JsonMappingException) {
//        }
//    }
//
//    this["$name.yaml"] = HeavensBot.YAML_MAPPER.writeValueAsBytes(t)
//
//    return oldValue
//}

fun embed(init: KMessageBuilder.() -> Unit): EmbedObject {
    val embedBuilder = KMessageBuilder()
    embedBuilder.init()
    return embedBuilder.embed
}

fun message(init: KMessageBuilder.() -> Unit): Mono<Message> {
    val builder = KMessageBuilder()
    builder.init()
    return builder.send()
}

fun messageBlock(init: KMessageBuilder.() -> Unit): Message? {
    val builder = KMessageBuilder()
    builder.init()
    return builder.send().block()
}

fun messageSubscribe(init: KMessageBuilder.() -> Unit) {
    val builder = KMessageBuilder()
    builder.init()
    builder.send().subscribe()
}