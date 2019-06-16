package com.dueeeke.httpserver

import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SimpleHttpServer(webConfiguration: WebConfiguration) {

    private var mIsStart = false

    private val mWebConfiguration: WebConfiguration = webConfiguration

    private var mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    private lateinit var mServerSocket: ServerSocket

    private val mHandlers: HashSet<IResourceUriHandler> = HashSet()

    private val tag = "HttpServer"

    /**
     * 启动服务器
     */
    fun startAsync() {
        mIsStart = true
        Thread(Runnable {
            doProcessSync()
        }).start()
    }

    /**
     * 停止服务器
     */
    fun stopAsync() {
        if (mIsStart) {
            mIsStart = false
            mServerSocket.close()
        }
    }

    private fun doProcessSync() {
        try {
            mServerSocket = ServerSocket()
            val socketAddress = InetSocketAddress(mWebConfiguration.port)
            mServerSocket.bind(socketAddress)
            while (mIsStart) {
                val remotePeer = mServerSocket.accept()
                mThreadPool.submit {
                    onAcceptRemotePeer(remotePeer)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun onAcceptRemotePeer(remotePeer: Socket) {
        try {
            val httpContext = HttpContext()
            httpContext.mCurrentPeer = remotePeer
            val ins = remotePeer.getInputStream()
            var headerLine: String
            //读取状态行中的资源地址
            val resourceUri = StreamToolKit.readLine(ins).toString().split(" ")[1]
            //读取请求头信息
            while (StreamToolKit.readLine(ins).also { headerLine = it.toString() } != null) {
                if (headerLine == "\r\n") {
                    break
                }
                val pair = headerLine.split(": ")
                if (pair.size > 1) {
                    //保存请求头信息
                    httpContext.addRequestHeader(pair[0], pair[1])
                }
                Log.d(tag, "header Line $headerLine")
            }

            for (handler in mHandlers) {
                if (!handler.accept(resourceUri)) {
                    continue
                }
                //处理请求
                handler.handle(resourceUri, httpContext)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun registerResourceHandler(handler: IResourceUriHandler) {
        mHandlers.add(handler)
    }
}