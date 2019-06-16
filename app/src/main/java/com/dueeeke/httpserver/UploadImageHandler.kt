package com.dueeeke.httpserver

import java.io.ByteArrayOutputStream
import java.io.PrintWriter

/**
 * 往服务器传输图片
 */
open class UploadImageHandler : IResourceUriHandler {
    private val acceptPrefix = "/upload_image/"
    override fun accept(uri: String): Boolean {
        return uri.startsWith(acceptPrefix)
    }

    override fun handle(uri: String, httpContext: HttpContext) {
        val totalLength = httpContext.getRequestHeaderValue("content-length")?.toLong()
        val baops = ByteArrayOutputStream()
        val inputStream = httpContext.mCurrentPeer!!.getInputStream()
        var read = 0
        var leftLength = totalLength
        val buffer = ByteArray(102400)
        while (leftLength!! > 0 && inputStream.read(buffer).also { read = it } > 0) {
            baops.write(buffer, 0, read)
            leftLength -= read
        }
        baops.flush()
        baops.close()
        //向客户端返回请求信息
        val ops = httpContext.mCurrentPeer?.getOutputStream()
        val writer = PrintWriter(ops!!)
        writer.println("HTTP/1.1 200 OK")
        writer.println()
        writer.flush()
        writer.close()
        onImageLoaded(baops)
    }


    open fun onImageLoaded(outputStream: ByteArrayOutputStream) {

    }
}