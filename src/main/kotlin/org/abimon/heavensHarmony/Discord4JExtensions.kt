package org.abimon.heavensHarmony

import sx.blah.discord.util.RequestBuffer

fun <T> buffer(action: () -> T): RequestBuffer.RequestFuture<T> = RequestBuffer.request(action)
fun <T> bufferAndWait(action: () -> T): T = RequestBuffer.request(action).get()