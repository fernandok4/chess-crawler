package com.kanashiro

import com.kanashiro.job.ChessCrawlerJob
import java.util.concurrent.Executors
import java.util.concurrent.Future

class Application {

    companion object {
        private val executor = Executors.newFixedThreadPool(10)
        private val future: MutableList<Future<*>> = mutableListOf()
        @JvmStatic
        fun main(args: Array<String>) {
            for(i in 1013642L..1014642L){
                future.add(executor.submit(ChessCrawlerJob(i)))
            }
        }
    }
}