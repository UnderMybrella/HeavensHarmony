package org.abimon.heavens_harmony

import discord4j.common.jackson.Possible
import discord4j.common.json.EmbedFieldEntity
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.MessageChannel
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.MessageEvent
import discord4j.core.event.domain.message.MessageUpdateEvent
import discord4j.rest.json.request.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

//Type Aliases

typealias MenuOperation<T> = (Message, ReactionEmoji, User) -> Mono<T>

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

fun MessageChannel.sendMessage(init: KMessageBuilder.() -> Unit): Mono<Message> {
    val builder = KMessageBuilder()
    builder.init()
    builder.channel = this.toMono()
    return builder.send()
}

fun Mono<MessageChannel>.sendMessage(init: KMessageBuilder.() -> Unit): Mono<Message> {
    val builder = KMessageBuilder()
    builder.init()
    builder.channel = this.toMono()
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

fun Message.editMessage(init: KMessageEditBuilder.() -> Unit): Mono<Message> {
    val builder = KMessageEditBuilder()
    builder.init()
    builder.message = this
    return builder.edit()
}

fun Mono<Message>.editMessage(init: KMessageEditBuilder.() -> Unit): Mono<Message> {
    return this.flatMap { msg ->
        val builder = KMessageEditBuilder()
        builder.init()
        builder.message = msg
        builder.edit()
    }
}

fun makeEmbed(init: KMessageBuilder.() -> Unit): EmbedObject {
    val embedBuilder = KMessageBuilder()
    embedBuilder.init()
    return embedBuilder.embed
}

fun <T> isNull(t: T): Boolean = t == null
fun <T> isNotNull(t: T): Boolean = t != null

fun <T> possibleOf(t: T?): Possible<T> = if (t == null) Possible.absent() else Possible.of(t)

fun EmbedObject.toRequest(): EmbedRequest = EmbedRequest(
        possibleOf(title), possibleOf(description), possibleOf(url), possibleOf(timestamp),
        possibleOf(color.takeUnless((KMessageBuilder.BUT_NOT_BLACK::equals))),
        possibleOf(footer?.toRequest()), possibleOf(image?.toRequest()),
        possibleOf(thumbnail?.toRequest()), possibleOf(author?.toRequest()),
        possibleOf(fields?.map(EmbedObject.EmbedFieldObject::toEntity)?.toTypedArray())
)

fun EmbedObject.FooterObject.toRequest(): EmbedFooterRequest? = text?.let { text -> EmbedFooterRequest(text, icon_url) }
fun EmbedObject.ImageObject.toRequest(): EmbedImageRequest? = url?.let(::EmbedImageRequest)
fun EmbedObject.ThumbnailObject.toRequest(): EmbedThumbnailRequest? = url?.let(::EmbedThumbnailRequest)
fun EmbedObject.AuthorObject.toRequest(): EmbedAuthorRequest? = name?.let { name -> EmbedAuthorRequest(name, url, icon_url) }
fun EmbedObject.EmbedFieldObject.toEntity(): EmbedFieldEntity? = name?.let { name -> value?.let { value -> EmbedFieldEntity(name, value, inline) } }
