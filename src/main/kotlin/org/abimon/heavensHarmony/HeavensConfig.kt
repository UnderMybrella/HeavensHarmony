package org.abimon.heavensHarmony

open class HeavensConfig(
        val token: String,
        val ownerID: Long,

        val databaseIP: String,
        val databaseUser: String,
        val databasePass: String,

        val databaseCloudProxy: String?,

        val rsaPrivateKey: String,
        val rsaPublicKey: String,

        val defaultPrefix: String = "~|"
)