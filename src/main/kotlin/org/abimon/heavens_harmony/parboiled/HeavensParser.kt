package org.abimon.heavens_harmony.parboiled

import org.parboiled.Action
import org.parboiled.BaseParser
import org.parboiled.Parboiled
import org.parboiled.Rule
import org.parboiled.annotations.BuildParseTree
import org.parboiled.support.Var

@BuildParseTree
open class HeavensParser(parboiledCreated: Boolean) : BaseParser<Any>() {
    companion object {
        operator fun invoke(): HeavensParser = Parboiled.createParser(HeavensParser::class.java, true)
    }

    open val whitespace = (Character.MIN_VALUE until Character.MAX_VALUE).filter { Character.isWhitespace(it) }.toCharArray()

    open fun WhitespaceCharacter(): Rule = AnyOf(whitespace)
    open fun OptionalWhitespace(): Rule = ZeroOrMore(WhitespaceCharacter())
    open fun Whitespace(): Rule = OneOrMore(WhitespaceCharacter())
    open fun InlineWhitespaceCharacter(): Rule = AnyOf(charArrayOf('\t', ' '))
    open fun InlineWhitespace(): Rule = OneOrMore(InlineWhitespaceCharacter())
    open fun OptionalInlineWhitespace(): Rule = ZeroOrMore(InlineWhitespaceCharacter())

    open val digitsLower = charArrayOf(
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    )

    open val digitsUpper = charArrayOf(
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    )

    open fun Digit(): Rule = Digit(10)
    open fun Digit(base: Int): Rule = FirstOf(AnyOf(digitsLower.sliceArray(0 until base)), AnyOf(digitsUpper.sliceArray(0 until base)))

    open fun Parameter(): Rule {
        val str = Var<String>()

        return FirstOf(
                Sequence(
                        "\"",
                        Action<Any> { str.set("") },
                        Optional(
                                OneOrMore(
                                        FirstOf(
                                                Sequence(
                                                        "\\",
                                                        FirstOf(
                                                                Sequence(
                                                                        FirstOf(
                                                                                "\"",
                                                                                "\\",
                                                                                "/",
                                                                                "b",
                                                                                "f",
                                                                                "n",
                                                                                "r",
                                                                                "t"
                                                                        ),
                                                                        Action<Any> {
                                                                            when (match()) {
                                                                                "\"" -> str.set(str.get() + "\"")
                                                                                "\\" -> str.set(str.get() + "\\")
                                                                                "/" -> str.set(str.get() + "/")
                                                                                "b" -> str.set(str.get() + "\b")
                                                                                "f" -> str.set(str.get() + 0xC.toChar())
                                                                                "n" -> str.set(str.get() + "\n")
                                                                                "r" -> str.set(str.get() + "\r")
                                                                                "t" -> str.set(str.get() + "\t")
                                                                            }

                                                                            return@Action true
                                                                        }
                                                                ),
                                                                Sequence(
                                                                        "u",
                                                                        NTimes(4, Digit(16)),
                                                                        Action<Any> { str.set(str.get() + match().toInt(16).toChar()) }
                                                                )
                                                        )
                                                ),
                                                Sequence(
                                                        AllButMatcher(charArrayOf('\\', '"')),
                                                        Action<Any> { str.set(str.get() + match()) }
                                                )
                                        )
                                )
                        ),
                        Action<Any> { push(str.get()) },
                        "\""
                ),
                Sequence(
                        Action<Any> { str.set("") },
                        Optional(
                                OneOrMore(
                                        FirstOf(
                                                Sequence(
                                                        "\\",
                                                        FirstOf(
                                                                Sequence(
                                                                        FirstOf(
                                                                                "\"",
                                                                                "\\",
                                                                                "/",
                                                                                "b",
                                                                                "f",
                                                                                "n",
                                                                                "r",
                                                                                "t"
                                                                        ),
                                                                        Action<Any> {
                                                                            when (match()) {
                                                                                "\"" -> str.set(str.get() + "\"")
                                                                                "\\" -> str.set(str.get() + "\\")
                                                                                "/" -> str.set(str.get() + "/")
                                                                                "b" -> str.set(str.get() + "\b")
                                                                                "f" -> str.set(str.get() + 0xC.toChar())
                                                                                "n" -> str.set(str.get() + "\n")
                                                                                "r" -> str.set(str.get() + "\r")
                                                                                "t" -> str.set(str.get() + "\t")
                                                                            }

                                                                            return@Action true
                                                                        }
                                                                ),
                                                                Sequence(
                                                                        "u",
                                                                        NTimes(4, Digit(16)),
                                                                        Action<Any> { str.set(str.get() + match().toInt(16).toChar()) }
                                                                )
                                                        )
                                                ),
                                                Sequence(
                                                        AllButMatcher(whitespace.plus(charArrayOf('\\', '|'))),
                                                        Action<Any> { str.set(str.get() + match()) }
                                                )
                                        )
                                )
                        ),
                        Action<Any> { push(str.get()) }
                )
        )
    }

    open fun <T : Any> Perform(op: () -> T?): Action<Any> = Action { op() != null }
    open fun Push(value: Any? = null): Action<Any> = Action { push(value ?: match()) }
}