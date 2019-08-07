package org.abimon.heavens_harmony

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NamedThreadFactory(val nameTemplate: String, val daemon: Boolean = false, val priority: Int = Thread.NORM_PRIORITY) : ThreadFactory {
    private var group: ThreadGroup = System.getSecurityManager()?.threadGroup ?: Thread.currentThread().threadGroup
    private val threadNumber = AtomicInteger(1)

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group, r, nameTemplate.replace("%thread", threadNumber.getAndIncrement().toString()), 0)
        t.isDaemon = daemon
        t.priority = priority
        return t
    }
}