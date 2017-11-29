package org.abimon.heavensHarmony.permissions

enum class EnumMemberStatus {
    OWNER,
    ADMINISTRATOR,
    MODERATOR,
    VERIFIED,
    USER;

    val permission: MemberStatusPermission by lazy {
        MemberStatusPermission(this)
    }
}