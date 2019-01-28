package org.abimon.heavens_harmony.parboiled

import org.parboiled.MatcherContext
import org.parboiled.matchers.AnyMatcher
import org.parboiled.support.Chars

object ParamMatcher : AnyMatcher() {
    override fun match(context: MatcherContext<*>): Boolean {
        return when (context.currentChar) {
            '"' -> {
                if (context.inputBuffer.charAt(context.currentIndex - 1) == '\\')
                    return super.match(context)
                return false
            }
            Chars.EOI -> false
            else -> super.match(context)
        }
    }
}