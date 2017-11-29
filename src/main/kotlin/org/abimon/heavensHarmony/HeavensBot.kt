package org.abimon.heavensHarmony

import org.abimon.dArmada.DiscordScout
import org.abimon.imperator.handle.Imperator
import org.abimon.imperator.handle.Scout
import org.abimon.imperator.impl.BasicImperator
import sx.blah.discord.api.IDiscordClient

abstract class HeavensBot {
    abstract val client: IDiscordClient
    abstract val config: HeavensConfig
    abstract val database: Database
    abstract val encryption: EncryptionWrapper

    val imperator: Imperator = BasicImperator()
    val discordScout: Scout = DiscordScout()
}