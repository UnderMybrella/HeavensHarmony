package org.abimon.heavens_harmony

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import discord4j.core.DiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import org.abimon.heavens_harmony.parboiled.HeavensParser
import org.abimon.heavens_harmony.parboiled.ParboiledAngel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

abstract class HeavensBot {
    companion object {
        val MAPPER: ObjectMapper = ObjectMapper()
                .registerKotlinModule()
                .registerModules(Jdk8Module(), JavaTimeModule(), ParameterNamesModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

        val YAML_MAPPER: ObjectMapper = ObjectMapper(YAMLFactory())
                .registerKotlinModule()
                .registerModules(Jdk8Module(), JavaTimeModule(), ParameterNamesModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

        var INSTANCE: HeavensBot? = null
        val INSTANCE_CONFIG: HeavensConfig?
            get() = INSTANCE?.config
    }

    abstract val config: HeavensConfig
    abstract val database: JDBCDatabase
    abstract val encryption: EncryptionWrapper
    abstract val parser: HeavensParser

    val angels: MutableList<ParboiledAngel<*>> = ArrayList()
    open val logger: Logger = LoggerFactory.getLogger("HeavensBot")

    fun hireAngels(client: DiscordClient, afterlife: Any) {
        afterlife.javaClass.kotlin.memberProperties.forEach { recruit ->
            if((recruit.returnType.classifier as? KClass<*>)?.isSubclassOf(ParboiledAngel::class) == true || recruit.returnType.classifier == ParboiledAngel::class) {
                hireAngel(client, recruit.get(afterlife) as? ParboiledAngel<*> ?: return@forEach)
            }
        }
    }

    fun <T> hireAngel(client: DiscordClient, angel: ParboiledAngel<T>) {
        angels.add(angel)

        client.eventDispatcher.on(MessageCreateEvent::class.java)
                .log()
                .doOnError(Throwable::printStackTrace)
                .doOnNext { logger.trace("Received MessageCreateEvent; angel is subscribed: {}", angel in angels) }
                .filter { angel in angels }
                .filter { event -> event.message.author.map { user -> !user.isBot }.orElse(false).also { isBot -> logger.trace("Message sent by member: {}", isBot) } }
                .filterWhen(angel::shouldAcceptMessage)
                .flatMap(angel::acceptMessage)
                .subscribe(angel::acceptedMessage)
    }
}