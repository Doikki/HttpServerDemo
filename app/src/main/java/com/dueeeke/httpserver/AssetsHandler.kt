package com.dueeeke.httpserver

import android.content.Context
import java.io.PrintStream

/**
 * 访问服务器上的静态资源
 */
class AssetsHandler(private val context: Context) : IResourceUriHandler {

    private val acceptPrefix = "/static/"
    override fun accept(uri: String): Boolean {
        return uri.startsWith(acceptPrefix)
    }

    override fun handle(uri: String, httpContext: HttpContext) {
        val startIndex = acceptPrefix.length
        val assetsPath = uri.substring(startIndex)
        val fis = context.assets.open(assetsPath)
        val raw = StreamToolKit.readRowFromStream(fis)
        fis.close()
        val ops = httpContext.mCurrentPeer?.getOutputStream()
        val printer = PrintStream(ops)
        printer.println("HTTP/1.1 200 OK")
        printer.println("Content-Length:" + raw.size)
        when {
            assetsPath.endsWith(".html") -> printer.println("Content-Type: text/html; charset=utf-8")
            assetsPath.endsWith(".htm") -> printer.println("Content-Type: text/html; charset=utf-8")
            assetsPath.endsWith(".js") -> printer.println("Content-Type: application/javascript; charset=utf-8")
            assetsPath.endsWith(".css") -> printer.println("Content-Type: text/css")
            assetsPath.endsWith(".jpg") -> printer.println("Content-Type: image/jpeg")
            assetsPath.endsWith(".png") -> printer.println("Content-Type: image/png")
        }
        printer.println()
        printer.write(raw)
        printer.flush()
        printer.close()
    }
}