package com.dueeeke.httpserver

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object StreamToolKit {
    @Throws(IOException::class)
    fun readLine(ins: InputStream): String? {
        val sb = StringBuilder()
        var c1 = 0
        var c2 = 0
        while (c2 != -1 && !(c1 == '\r'.toInt() && c2 == '\n'.toInt())) {
            c1 = c2
            c2 = ins.read()
            sb.append(c2.toChar())
        }
        return if (sb.isEmpty()) {
            null
        } else sb.toString()
    }

    @Throws(IOException::class)
    fun readRowFromStream(fis: InputStream): ByteArray {
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(10240)
        var read: Int
        while (fis.read(buffer).also { read = it } > 0) {
            bos.write(buffer, 0, read)
        }
        return bos.toByteArray()
    }
}
