package org.abimon.heavensHarmony

import org.apache.http.message.BasicNameValuePair
import sx.blah.discord.api.internal.DiscordEndpoints
import sx.blah.discord.api.internal.Requests
import sx.blah.discord.api.internal.json.responses.ApplicationInfoResponse

open class HeavensConfig(
        val token: String,
        val ownerID: Long,

        val databaseURL: String,
        val databaseUser: String?,
        val databasePass: String?,

        val databaseCloudProxy: String?,

        val createRSAKeys: Boolean = false,
        val rsaKeysizeGen: Int = 2048,
        val rsaPrivateKey: String,
        val rsaPublicKey: String,

        val defaultPrefix: String = "~|"
) {
    val applicationID: Long = bufferAndWait { java.lang.Long.parseUnsignedLong(Requests.GENERAL_REQUESTS.GET.makeRequest(DiscordEndpoints.APPLICATIONS + "/@me", ApplicationInfoResponse::class.java, BasicNameValuePair("Authorization", "Bot $token")).id) }
}