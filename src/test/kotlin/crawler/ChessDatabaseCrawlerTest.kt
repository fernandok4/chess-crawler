package crawler

import com.kanashiro.crawler.ChessDatabaseCrawler
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on
import java.io.File

class ChessDatabaseCrawlerTest: Spek({

    val chessDatabaseCrawler = ChessDatabaseCrawler()

    describe("getGameMoves"){
        on("getGameMoves teste.html"){
            val file = File("src/test/kotlin/crawler/teste.html")
            chessDatabaseCrawler.getGameMoves(file.readText())
        }
    }

    describe("readGame"){
        on("readGame"){
            chessDatabaseCrawler.readGame(1013852)
        }
    }
})