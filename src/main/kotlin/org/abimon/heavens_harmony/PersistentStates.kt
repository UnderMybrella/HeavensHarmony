package org.abimon.heavens_harmony

import java.io.*

class PersistentStates(val bot: HeavensBot) {
    val db: JDBCDatabase = bot.database

    operator fun get(key: String): Any? {
        val hash = key.sha256Hash()
        return db.use { connection ->
            val select = connection.prepareStatement("SELECT state_value FROM persistent WHERE id=?;")
            select.setString(1, hash)
            select.execute()

            val results = select.resultSet

            if (results.next()) {
                val serial = results.getBytes("value")
                val inputStream = ObjectInputStream(ByteArrayInputStream(serial))
                return@use inputStream.readObject()
            } else {
                return@use null
            }
        }
    }

    operator fun set(key: String, value: Serializable?) {
        val hash = key.sha256Hash()

        db.use { connection ->
            if(value == null) {
                val delete = connection.prepareStatement("DELETE FROM persistent WHERE id=?;")
                delete.setString(1, hash)
                delete.execute()
            } else {
                val data = ByteArrayOutputStream()
                val outputStream = ObjectOutputStream(data)
                outputStream.writeObject(value)

                val insert = connection.prepareStatement("INSERT INTO persistent (id, state_value) VALUES(?, ?) ON DUPLICATE KEY UPDATE state_value=VALUES(state_value);")
                insert.setString(1, hash)
                insert.setBytes(2, data.toByteArray())
                insert.execute()
            }
        }
    }

    init {
        db.use { connection -> connection.createStatement().execute("CREATE TABLE IF NOT EXISTS persistent (id VARCHAR(64) PRIMARY KEY NOT NULL, state_value BLOB NOT NULL);") }
    }
}