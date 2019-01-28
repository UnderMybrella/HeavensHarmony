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

open class ParboiledAngel<T>(val bot: HeavensBot, val rule: Rule, val errorOnEmpty: Boolean = true, val afterAcceptance: (T) -> Unit = {}, val command: (MessageCreateEvent, List<Any>) -> Publisher<T>) {
    private val pool: ObjectPool<ParseRunner<Any>> = GenericObjectPool(PooledParseRunnerObjectFactory(rule))

    fun shouldAcceptMessage(event: MessageCreateEvent): Boolean {
        val runner = pool.borrowObject()
        try {
            val result = event.message.content.map(runner::run)

            return result.map(ParsingResult<*>::matched).orElse(!errorOnEmpty)
        } finally {
            pool.returnObject(runner)
        }
    }

    fun acceptMessage(event: MessageCreateEvent): Publisher<T> {
        val runner = pool.borrowObject()
        return try {
            val result = event.message.content.map(runner::run)

            if (result.map(ParsingResult<*>::matched).orElse(!errorOnEmpty)) {
                command(event, result.map(ParsingResult<*>::valueStack).map(ValueStack<*>::toList).orElse(emptyList()))
            } else {
                Mono.empty()
            }
        } finally {
            pool.returnObject(runner)
        }
    }

    fun acceptedMessage(t: T): Unit = afterAcceptance(t)
}