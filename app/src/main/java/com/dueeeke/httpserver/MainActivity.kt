package com.dueeeke.httpserver

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var mHttpServer: SimpleHttpServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webConfiguration = WebConfiguration(8088)
        mHttpServer = SimpleHttpServer(webConfiguration)
        mHttpServer.registerResourceHandler(AssetsHandler(this))
        mHttpServer.registerResourceHandler(object : UploadImageHandler() {
            override fun onImageLoaded(outputStream: ByteArrayOutputStream) {
                runOnUiThread {
                    image.setImageBitmap(BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size()))
                }
            }
        })
        mHttpServer.startAsync()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHttpServer.stopAsync()
    }
}
