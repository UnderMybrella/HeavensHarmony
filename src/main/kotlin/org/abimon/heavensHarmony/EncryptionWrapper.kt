package org.abimon.heavensHarmony

import org.abimon.visi.security.*
import java.io.File
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.sql.Statement
import java.util.*

class EncryptionWrapper(val bot: HeavensBot) {
    val db: String = "heavens_${bot.config.applicationID}"
    val database: Database = bot.database

    val privateKey: PrivateKey
        get() = RSAPrivateKey(File(bot.config.rsaPrivateKey).readText(Charsets.UTF_8))

    val publicKey: PublicKey
        get() = RSAPublicKey(File(bot.config.rsaPublicKey).readText(Charsets.UTF_8))

    private val secureRandom = SecureRandom()

    fun encrypt(data: ByteArray, server: Long, file: String): ByteArray
            = data.encryptAES(file.md5HashData(), getKeyFor(server))

    fun decrypt(data: ByteArray, server: Long, file: String): ByteArray
            = data.decryptAES(file.md5HashData(), getKeyFor(server))

    fun createTable() {
        (database connectionFor db).use { connection -> connection.createStatement().createAESTable() }
    }

    fun Statement.createAESTable()
            = this.execute("CREATE TABLE IF NOT EXISTS aes_keys (server VARCHAR(63) PRIMARY KEY UNIQUE NOT NULL, aes_key VARBINARY(1024) NOT NULL);")

    fun getKeyFor(server: Long): ByteArray {
        (database connectionFor db).use { connection ->
            connection.createStatement().createAESTable()

            val select = connection.prepareStatement("SELECT * FROM aes_keys WHERE server=?;")
            select.setString(1, "$server")

            select.execute()
            val selectResults = select.resultSet

            if (selectResults.next())
                return selectResults.getBytes("aes_key").decryptRSA(privateKey)

            val key = ByteArray(16)
            secureRandom.nextBytes(key)

            val insert = connection.prepareStatement("INSERT INTO aes_keys (server, aes_key) VALUES(?, ?);")
            insert.setString(1, "$server")
            insert.setBytes(2, key.encryptRSA(publicKey))

            insert.execute()

            return key
        }
    }

    fun decodeAESKey(aesKey: String): ByteArray
            = Base64.getDecoder().decode(aesKey).decryptRSA(privateKey)

    fun encodeAESKey(aesKey: ByteArray): String
            = Base64.getEncoder().encodeToString(aesKey.encryptRSA(publicKey))

    fun String.md5HashData(): ByteArray
            = MessageDigest.getInstance("MD5").digest(this.toByteArray(Charsets.UTF_8))

    fun ByteArray.md5HashData(): ByteArray
            = MessageDigest.getInstance("MD5").digest(this)
}