package org.abimon.heavensHarmony

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.abimon.dArmada.DiscordScout
import org.abimon.imperator.handle.Imperator
import org.abimon.imperator.handle.Scout
import org.abimon.imperator.impl.BasicImperator
import sx.blah.discord.api.IDiscordClient

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

    abstract val client: IDiscordClient
    abstract val config: HeavensConfig
    abstract val database: JDBCDatabase
    abstract val encryption: EncryptionWrapper

    val imperator: Imperator = BasicImperator()
    val discordScout: Scout = DiscordScout().apply { imperator.hireScout(this) }
}