package org.abimon.heavens_harmony

open class HeavensConfig(
        val token: String,
        val ownerID: Long,

        val databaseURL: String,
        val databaseUser: String?,
        val databasePass: String?,

        val databaseCloudProxy: String?,

        val rsaPrivateKey: String,
        val rsaPublicKey: String,

        val defaultPrefix: String = "~|"
)