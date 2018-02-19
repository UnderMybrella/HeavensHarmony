package org.abimon.heavensHarmony

import java.io.*

class PersistentStates(val bot: HeavensBot) {
    val db: Database = bot.database

    operator fun get(key: String): Any? {
        db.use { connection ->
            val select = connection.prepareStatement("SELECT * FROM persistent WHERE id=?;")
            select.setString(1, key)
            select.execute()

            val results = select.resultSet

            if (results.next()) {
                val serial = results.getBytes("value")
                val inputStream = ObjectInputStream(ByteArrayInputStream(serial))
                return inputStream.readObject()
            }
        }

        return null
    }

    operator fun set(key: String, value: Serializable?) {
        db.use { connection ->
            if(value == null) {
                val delete = connection.prepareStatement("DELETE FROM persistent WHERE id=?;")
                delete.setString(1, key)
                delete.execute()
            } else {
                val data = ByteArrayOutputStream()
                val outputStream = ObjectOutputStream(data)
                outputStream.writeObject(value)

                val insert = connection.prepareStatement("INSERT INTO persistent (id, value) VALUES(?, ?) ON DUPLICATE KEY UPDATE value=VALUES(value);")
                insert.setString(1, key)
                insert.setBytes(2, data.toByteArray())
                insert.execute()
            }
        }
    }

    init {
        db.use { connection -> connection.createStatement().execute("CREATE TABLE IF NOT EXISTS persistent (id VARCHAR(255) PRIMARY KEY NOT NULL, value BLOB NOT NULL);") }
    }
}