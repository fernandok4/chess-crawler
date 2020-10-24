package com.kanashiro.http

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