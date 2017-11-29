package org.abimon.heavensHarmony.permissions

@Suppress("ArrayInDataClass")
data class JacksonPermissionObject(
        val name: String,
        val role_ids: Array<Long>?,
        val role_names: Array<String>?,
        val user_ids: Array<Long>?,
        val user_names: Array<String>?,
        val channels: Array<Long>?,
        val permissions: Array<JacksonPermissionNode>
)

data class JacksonPermissionNode(val state: EnumJsonState, val permission: List<String>) {
    constructor(enumState: EnumJsonState, perm: String): this(enumState, perm.split('.'))
    constructor(perm: String): this(if(perm.startsWith("-")) EnumJsonState.DENY else EnumJsonState.GRANT, if(perm.startsWith("-")) perm.substring(1).split('.') else perm.split('.'))

    fun match(perm: String): EnumJsonState {
        if(permission.isEmpty())
            return state

        val components = perm.split('.')
        val max = components.size.coerceAtMost(permission.size)

        for(i in 0 until max)
            if(permission[i] == "*")
                return state
            else if(permission[i].endsWith("*") && components[i].startsWith(permission[i].substringBeforeLast('*')))
                return state
            else if(permission[i] != components[i])
                return EnumJsonState.CONTINUE

        if(components.size != permission.size)
            return EnumJsonState.CONTINUE
        return state
    }
}