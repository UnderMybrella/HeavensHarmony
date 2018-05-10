package org.abimon.heavensHarmony

import org.abimon.visi.security.*
import java.io.File
import java.security.*
import java.sql.Statement
import java.util.*
import kotlin.collections.HashMap

class EncryptionWrapper(val bot: HeavensBot) {
    val database: JDBCDatabase = bot.database

    val privateKey: PrivateKey
        get() = RSAPrivateKey(File(bot.config.rsaPrivateKey).readText(Charsets.UTF_8))

    val publicKey: PublicKey
        get() = RSAPublicKey(File(bot.config.rsaPublicKey).readText(Charsets.UTF_8))

    private val secureRandom = SecureRandom()
    private val tmpKeyStore = File("tmp_key_store")
    private val tmpRSAKeys: MutableMap<Long, PrivateKey> = HashMap()

    fun encrypt(data: ByteArray, server: Long, file: String): ByteArray = data.encryptAES(file.md5HashData(), getKeyFor(server))

    fun decrypt(data: ByteArray, server: Long, file: String): ByteArray = data.decryptAES(file.md5HashData(), getKeyFor(server))

    fun createTable() {
        database.use { connection -> connection.createStatement().createAESTable() }
    }

    fun Statement.createAESTable() = this.execute("CREATE TABLE IF NOT EXISTS aes_keys (server VARCHAR(63) PRIMARY KEY UNIQUE NOT NULL, aes_key VARBINARY(1024) NOT NULL);")

    fun getKeyFor(server: Long): ByteArray {
        val keyFile = File(tmpKeyStore, "$server-key.dat")
        if (server in tmpRSAKeys && keyFile.exists()) {
            val rsa = tmpRSAKeys[server]!!

            return keyFile.readBytes().decryptRSA(rsa)
        }

        return database.use { connection ->
            connection.createStatement().createAESTable()

            val select = connection.prepareStatement("SELECT * FROM aes_keys WHERE server=?;")
            select.setString(1, "$server")

            select.execute()
            val selectResults = select.resultSet

            val aesKey: ByteArray

            if (selectResults.next()) {
                aesKey = selectResults.getBytes("aes_key").decryptRSA(privateKey)
            } else {
                aesKey = ByteArray(16)
                secureRandom.nextBytes(aesKey)

                val insert = connection.prepareStatement("INSERT INTO aes_keys (server, aes_key) VALUES(?, ?);")
                insert.setString(1, "$server")
                insert.setBytes(2, aesKey.encryptRSA(publicKey))

                insert.execute()
            }

            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(1024, secureRandom)

            val pair = keyGen.genKeyPair()

            tmpRSAKeys[server] = pair.private
            keyFile.writeBytes(aesKey.encryptRSA(pair.public))

            return@use aesKey
        }
    }

    fun decodeAESKey(aesKey: String): ByteArray = Base64.getDecoder().decode(aesKey).decryptRSA(privateKey)

    fun encodeAESKey(aesKey: ByteArray): String = Base64.getEncoder().encodeToString(aesKey.encryptRSA(publicKey))

    fun String.md5HashData(): ByteArray = MessageDigest.getInstance("MD5").digest(this.toByteArray(Charsets.UTF_8))

    fun ByteArray.md5HashData(): ByteArray = MessageDigest.getInstance("MD5").digest(this)

    init {
        val private = File(bot.config.rsaPrivateKey)
        val public = File(bot.config.rsaPublicKey)

        if (!private.exists() && !public.exists())
            error("ERR: Encryption Keys do not exist")

        if (!private.exists())
            error("ERR: Private Key does not exist.")

        if (!public.exists())
            error("ERR: Public Key does not exist")

        if (tmpKeyStore.exists())
            tmpKeyStore.deleteRecursively()
        tmpKeyStore.mkdir()

        Runtime.getRuntime().addShutdownHook(Thread {
            tmpKeyStore.deleteRecursively()
        })
    }
}