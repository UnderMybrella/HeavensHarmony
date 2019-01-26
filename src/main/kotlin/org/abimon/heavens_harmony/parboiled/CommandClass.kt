package org.abimon.heavens_harmony.parboiled

import org.abimon.heavens_harmony.HeavensBot
import org.parboiled.Rule

interface CommandClass {
    val bot: HeavensBot

    fun <T : Rule> makeRule(op: HeavensParser.() -> T): T {
        return bot.parser.op()
    }

//    fun ParboiledSoldier(rule: Rule, scope: String? = null, command: ParboiledSoldier.(List<Any>) -> Boolean): ParboiledSoldier = ParboiledSoldier(rule, scope, bot, command)
//    fun ParboiledSoldier(rule: Rule, scope: String? = null, help: String, command: ParboiledSoldier.(List<Any>) -> Boolean): ParboiledSoldier = ParboiledSoldier(rule, scope, bot, help, command)
}