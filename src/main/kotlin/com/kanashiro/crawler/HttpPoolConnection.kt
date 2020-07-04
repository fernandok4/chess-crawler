package com.kanashiro.crawler

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

class HttpPoolConnection {



    companion object {
        val poolManager by lazy {
            val poolManager = PoolingHttpClientConnectionManager()
            poolManager.maxTotal = 8
            poolManager.defaultMaxPerRoute = 8
            poolManager
        }
    }
}