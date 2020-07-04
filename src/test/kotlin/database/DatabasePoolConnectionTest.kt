package database

import com.kanashiro.database.DatabasePoolConnection
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on

class DatabasePoolConnectionTest: Spek({
    describe("getConnection"){
        on("getConnection"){
            val conn = DatabasePoolConnection.getConnection()
            println(conn)
        }
    }
})