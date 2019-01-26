package org.abimon.heavens_harmony

data class GuildConfig(
        var prefix: String?,
        var command_aliases: Map<String, Any>?,
        
        var admin_role: Long?,
        var mod_role: Long?,
        var verified_role: Long?
)