package org.abimon.heavens_harmony.parboiled

import org.abimon.heavens_harmony.HeavensBot
import org.parboiled.Action
import org.parboiled.Rule
import org.parboiled.support.Var

interface CommandClass {
    val bot: HeavensBot

    fun <T : Rule> makeRule(op: HeavensParser.() -> T): T {
        return bot.parser.op()
    }

    fun <T : Rule, V : Any> makeRuleWith(default: () -> V, op: HeavensParser.(Var<V>) -> T): Rule {
        return makeRule {
            val ruleVar = Var<V>()
            Sequence(
                    Action<Any> { ruleVar.set(default()) },
                    bot.parser.op(ruleVar),
                    Action<Any> { push(ruleVar.get()) }
            )
        }
    }

//    fun ParboiledSoldier(rule: Rule, scope: String? = null, command: ParboiledSoldier.(List<Any>) -> Boolean): ParboiledSoldier = ParboiledSoldier(rule, scope, bot, command)
//    fun ParboiledSoldier(rule: Rule, scope: String? = null, help: String, command: ParboiledSoldier.(List<Any>) -> Boolean): ParboiledSoldier = ParboiledSoldier(rule, scope, bot, help, command)
}