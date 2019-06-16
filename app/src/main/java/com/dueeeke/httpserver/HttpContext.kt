package com.dueeeke.httpserver

import java.net.Socket

class HttpContext {

    var mCurrentPeer: Socket? = null

    private val mRequestHeaders: HashMap<String, String> = HashMap()

    fun addRequestHeader(headerName: String, headerValue: String) {
        mRequestHeaders[headerName] = headerValue.replace("\r\n", "")
    }

    fun getRequestHeaderValue(headerName: String): String? {
        return mRequestHeaders[headerName]
    }

}