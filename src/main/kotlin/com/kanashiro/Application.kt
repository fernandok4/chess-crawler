package com.kanashiro

import com.kanashiro.crawler.ChessDatabaseCrawler

class Application: Runnable {

    val crawler = ChessDatabaseCrawler()

    override fun run() {
        crawler.readGame(1013642)
    }
}