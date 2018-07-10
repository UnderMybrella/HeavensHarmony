package org.abimon.heavensHarmony

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import discord4j.core.DiscordClient

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

    abstract val client: DiscordClient
    abstract val config: HeavensConfig
    abstract val database: JDBCDatabase
    abstract val encryption: EncryptionWrapper
}