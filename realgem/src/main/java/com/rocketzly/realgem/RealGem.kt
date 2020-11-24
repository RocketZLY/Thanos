package com.rocketzly.realgem

import android.content.Context
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * User: Rocket
 * Date: 2020/11/19
 * Time: 4:20 PM
 */
class RealGem private constructor() {

    private lateinit var context: Context
    private var init = false
    private var buffer = ByteArray(1024 * 8)
    private var count = 0
    private lateinit var file: File

    companion object {
        const val FILE_NAME = "realGem"

        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RealGem()
        }
    }

    fun init(context: Context) {
        if (init) return
        init = true
        this.context = context
        this.file = context.getFileStreamPath(FILE_NAME)// ./data/data/pkgname/files/realGem
    }

    fun use(methodDesc: String) {
        if (!init) return

        val log = generateLog(methodDesc)
        val byteArray = log.toByteArray()
        val length = byteArray.size

        synchronized(this) {
            if (length > buffer.size) {//超出缓冲区大小直接写文件 理论上不会出现这种情况
                //写文件
                return
            }
            if (length + count > buffer.size) {//buffer写不下，写入文件
                flush()
            }

            //写入缓存
            System.arraycopy(byteArray, 0, buffer, count, length)
            count += length
        }
    }

    private fun generateLog(methodDesc: String): String {
        return "{\"method\": \"${methodDesc}\",\"time\": ${System.currentTimeMillis()},\"thread\": \"${Thread.currentThread()}\"}\n"
    }


    private fun flush() {
        val bos = BufferedOutputStream(FileOutputStream(file, true))
        bos.write(buffer, 0, count)
        bos.close()
        count = 0
    }
}