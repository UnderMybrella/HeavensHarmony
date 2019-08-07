package org.abimon.heavens_harmony

import reactor.util.function.Tuple2
import reactor.util.function.Tuple3

public operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = t1
public operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = t2

public operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component1(): T1 = t1
public operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component2(): T2 = t2
public operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component3(): T3 = t3

fun emptyErrorContinue(th: Throwable?, obj: Any?) {}