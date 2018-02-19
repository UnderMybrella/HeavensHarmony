package org.abimon.heavensHarmony

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class Database(val bot: HeavensBot) {
    val defaultDatabase: String = "heavens_${bot.config.applicationID}"
    val ds: HikariDataSource
    val heavensConfig: HeavensConfig
        get() = bot.config

    val connection: Connection
        get() = ds.connection

    init {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:mysql://${heavensConfig.databaseIP}:3306"
        config.username = heavensConfig.databaseUser
        config.password = heavensConfig.databasePass
        if (heavensConfig.databaseCloudProxy != null) {
            config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory")
            config.addDataSourceProperty("cloudSqlInstance", heavensConfig.databaseCloudProxy)
        }

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        ds = HikariDataSource(config)
    }

    infix fun connectionFor(db: String): Connection {
        val c = ds.connection
        c.createStatement().use { statement ->
            statement.execute("CREATE DATABASE IF NOT EXISTS $db;")
            statement.execute("USE $db;")
        }

        return c
    }

    public inline fun <R> use(block: (Connection) -> R): R {
        val c = ds.connection
        c.createStatement().use { statement ->
            statement.execute("CREATE DATABASE IF NOT EXISTS $defaultDatabase;")
            statement.execute("USE $defaultDatabase;")
        }

        return c.use(block)
    }


}