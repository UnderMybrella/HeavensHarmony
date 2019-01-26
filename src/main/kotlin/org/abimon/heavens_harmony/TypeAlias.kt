package org.abimon.heavens_harmony

import discord4j.core.`object`.entity.Message
import reactor.core.publisher.Flux

typealias MessageCommand=((Message, List<Any>) -> Flux<Any>)