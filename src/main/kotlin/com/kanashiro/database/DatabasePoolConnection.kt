package com.kanashiro.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.File
import java.sql.Connection
import java.util.*

class DatabasePoolConnection {
    companion object {
        private val ds: HikariDataSource by lazy {
            val config = HikariConfig()
            val file = File("database.properties")
            val properties = Properties()
            properties.load(file.inputStream())
            config.setJdbcUrl(properties.getProperty("CHESS_CRAWLER_JDBC_URL"))
            config.setUsername(properties.getProperty("CHESS_CRAWLER_USERNAME"))
            config.setPassword(properties.getProperty("CHESS_CRAWLER_PASSWORD"))
            config.addDataSourceProperty( "useSSL", "false")
            config.addDataSourceProperty("rewriteBatchedStatements", "true")
            config.connectionTimeout = 10000
            config.idleTimeout = 30000
            config.maximumPoolSize = 10
            config.isAutoCommit = true
            HikariDataSource(config)
        }

        fun getConnection(): Connection {
            return ds.connection
        }
    }
}