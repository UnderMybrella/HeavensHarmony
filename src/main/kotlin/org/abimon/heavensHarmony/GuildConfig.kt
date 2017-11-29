package org.abimon.heavensHarmony

data class GuildConfig(
        val prefix: String?,
        val command_aliases: Map<String, Any>?,
        
        val admin_role: Long?,
        val mod_role: Long?,
        val verified_role: Long?
)