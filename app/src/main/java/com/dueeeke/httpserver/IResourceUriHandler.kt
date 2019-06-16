package com.dueeeke.httpserver

interface IResourceUriHandler {
    fun handle(uri: String, httpContext: HttpContext)
    fun accept(uri: String): Boolean
}