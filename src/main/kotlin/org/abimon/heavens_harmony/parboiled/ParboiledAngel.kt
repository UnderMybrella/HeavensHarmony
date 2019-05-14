package org.abimon.heavens_harmony.parboiled

import discord4j.core.event.domain.message.MessageCreateEvent
import org.abimon.heavens_harmony.HeavensBot
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool
import org.parboiled.Rule
import org.parboiled.parserunners.ParseRunner
import org.parboiled.support.ParsingResult
import org.parboiled.support.ValueStack
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono

open class ParboiledAngel<T>(val bot: HeavensBot, val name: String, val rule: Rule, val errorOnEmpty: Boolean = true, val beforeAcceptance: (MessageCreateEvent) -> Mono<Boolean> = { Mono.just(true) }, val afterAcceptance: (T) -> Unit = {}, val command: (MessageCreateEvent, List<Any>) -> Publisher<T>) {
    private val pool: ObjectPool<ParseRunner<Any>> = GenericObjectPool(PooledParseRunnerObjectFactory(rule))

    fun shouldAcceptMessage(event: MessageCreateEvent): Publisher<Boolean> {
        val runner = pool.borrowObject()
        try {
            val result = event.message.content.map(runner::run)

            if (result.map(ParsingResult<*>::matched).orElse(!errorOnEmpty))
                return beforeAcceptance(event).doOnSuccess { accept -> bot.logger.trace("[$name] Should accept: $accept") }
            return Mono.just(false).doOnSuccess { accept -> bot.logger.trace("[$name] Did not match (${result.map(ParsingResult<*>::parseErrors).map { errors -> errors.joinToString { error -> "${error.errorMessage} [${error.startIndex}-${error.endIndex}]" } }}); should accept: $accept") }
        } finally {
            pool.returnObject(runner)
        }
    }

    fun acceptMessage(event: MessageCreateEvent): Publisher<T> {
        val runner = pool.borrowObject()
        return try {
            val result = event.message.content.map(runner::run)

            if (result.map(ParsingResult<*>::matched).orElse(!errorOnEmpty)) {
                bot.logger.trace("Accepting event")
                command(event, result.map(ParsingResult<*>::valueStack).map(ValueStack<*>::toList).map(List<Any>::asReversed).orElse(emptyList()))
            } else {
                bot.logger.trace("Did not match; not handling event")
                Mono.empty()
            }
        } finally {
            pool.returnObject(runner)
        }
    }

    fun acceptedMessage(t: T): Unit = afterAcceptance(t)
}