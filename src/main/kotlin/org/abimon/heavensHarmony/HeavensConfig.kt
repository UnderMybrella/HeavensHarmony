package org.abimon.heavensHarmony

open class HeavensConfig(
        val token: String,

        val databaseIP: String,
        val databaseUser: String,
        val databasePass: String,

        val databaseCloudProxy: String?,

        val rsaPrivateKey: String,
        val rsaPublicKey: String
)