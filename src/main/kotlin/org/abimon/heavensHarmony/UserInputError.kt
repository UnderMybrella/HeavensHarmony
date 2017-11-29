package org.abimon.heavensHarmony

import sx.blah.discord.api.internal.json.objects.EmbedObject

class UserInputError(val content: String, val embed: EmbedObject? = null): Exception()