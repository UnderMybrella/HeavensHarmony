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
        val ANGEL_CLASS = ParboiledAngel::class.java
    }

    abstract val client: DiscordClient
    abstract val config: HeavensConfig
    abstract val database: JDBCDatabase
    abstract val encryption: EncryptionWrapper
    abstract val parser: HeavensParser

    val angels: MutableList<ParboiledAngel<*>> = ArrayList()

    fun hireAngels(afterlife: Any) {
        afterlife::class.java.fields.filter { field -> ANGEL_CLASS.isAssignableFrom(field.type) }.forEach { field ->
            field.isAccessible = true
            hireAngel(ANGEL_CLASS.cast(field[afterlife]))
        }
    }

    fun hireAngel(angel: ParboiledAngel<*>) {
        angels.add(angel)

        client.eventDispatcher.on(MessageCreateEvent::class.java)
                .filter { angel in angels }
                .filterWhen { event -> event.message.author.map { user -> !user.isBot } }
                .filter(angel::acceptMessage)
                .flatMap(angel.command)
                .subscribe()
    }
}