package crawler

import com.kanashiro.crawler.ChessPgnMentorCrawler
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on

class ChessPgnMentorCrawlerTest: Spek({

    val crawler = ChessPgnMentorCrawler()

    describe("downloadFiles"){
        on("downloadFiles"){
            crawler.downloadFiles()
        }
    }
})