package org.abimon.heavens_harmony.parboiled

import discord4j.core.event.domain.message.MessageCreateEvent
import org.abimon.heavens_harmony.HeavensBot
import org.parboiled.Rule
import org.parboiled.parserunners.ReportingParseRunner
import org.parboiled.support.ParsingResult
import reactor.core.publisher.Mono

open class ParboiledAngel<T>(val bot: HeavensBot, val rule: Rule, val errorOnEmpty: Boolean = true, val command: (MessageCreateEvent) -> Mono<T>) {
    val runner = ReportingParseRunner<Any>(rule)

    fun acceptMessage(event: MessageCreateEvent): Boolean {
        runner.parseErrors.clear()
        val result = event.message.content.map(runner::run)

        return !result.map(ParsingResult<*>::hasErrors).orElse(errorOnEmpty)
    }
}