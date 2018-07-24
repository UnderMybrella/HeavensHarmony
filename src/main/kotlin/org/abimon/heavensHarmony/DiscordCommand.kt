package org.abimon.heavensHarmony

import discord4j.core.`object`.entity.Message
import org.parboiled.Rule
import org.parboiled.parserunners.ReportingParseRunner
import reactor.core.publisher.Mono

open class DiscordCommand<T>(val rule: Rule, val op: (Message, List<Any>) -> Mono<T>) {
    fun mapFrom(msg: Message): Mono<T> {
        val runner = ReportingParseRunner<Any>(rule)
        val result = runner.run(msg.content.orElse("").substringAfter('!'))
        if (result.hasErrors())
            return Mono.empty()

        return op(msg, result.valueStack.reversed())
    }
}