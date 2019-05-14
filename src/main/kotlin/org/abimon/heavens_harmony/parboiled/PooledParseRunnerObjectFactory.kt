package org.abimon.heavens_harmony.parboiled

import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.parboiled.Rule
import org.parboiled.parserunners.ParseRunner
import org.parboiled.parserunners.ReportingParseRunner
import org.parboiled.support.DefaultValueStack

open class PooledParseRunnerObjectFactory(val rule: () -> Rule): BasePooledObjectFactory<ParseRunner<Any>>() {
    /**
     * Wrap the provided instance with an implementation of
     * [PooledObject].
     *
     * @param obj the instance to wrap
     *
     * @return The provided instance, wrapped by a [PooledObject]
     */
    override fun wrap(obj: ParseRunner<Any>?): PooledObject<ParseRunner<Any>> = DefaultPooledObject(obj)

    /**
     * Creates an object instance, to be wrapped in a [PooledObject].
     *
     * This method **must** support concurrent, multi-threaded
     * activation.
     *
     * @return an instance to be served by the pool
     *
     * @throws Exception if there is a problem creating a new instance,
     * this will be propagated to the code requesting an object.
     */
    override fun create(): ParseRunner<Any> = ReportingParseRunner(rule())

    /**
     * Uninitializes an instance to be returned to the idle object pool.
     *
     * @param p a {@code PooledObject} wrapping the instance to be passivated
     *
     * @throws Exception if there is a problem passivating <code>obj</code>,
     *    this exception may be swallowed by the pool.
     *
     * @see #destroyObject
     */
    override fun passivateObject(p: PooledObject<ParseRunner<Any>>?) {
        p?.`object`?.withParseErrors(arrayListOf())
        p?.`object`?.withValueStack(DefaultValueStack())
    }
}