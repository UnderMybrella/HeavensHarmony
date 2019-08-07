package org.abimon.heavens_harmony

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

open class JDBCDatabase(val bot: HeavensBot) {
    val ds: HikariDataSource

    infix fun <T> use(op: (Connection) -> T): T = ds.connection.use(op)

    init {
        Class.forName("com.mysql.jdbc.Driver")

        val config = HikariConfig()
        config.jdbcUrl = bot.config.databaseURL

        config.username = bot.config.databaseUser
        config.password = bot.config.databasePass

        val cloudSqlInstance = bot.config.databaseCloudProxy

        if (cloudSqlInstance != null) {
            config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory")
            config.addDataSourceProperty("cloudSqlInstance", cloudSqlInstance)
        }

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        ds = HikariDataSource(config)
    }
}