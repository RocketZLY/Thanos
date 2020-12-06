package com.rocketzly.realgem.trace

import android.content.Context
import com.rocketzly.realgem.Utils
import com.rocketzly.realgem.db.RealGemDao
import java.util.concurrent.Executors

/**
 * 负责日志管理
 * User: Rocket
 * Date: 2020/12/4
 * Time: 2:25 PM
 */
class TraceLogManager(val context: Context) {

    companion object {
        private val executor = Executors.newSingleThreadExecutor {
            Thread(it, "realGem")
        }
    }

    private val dao = RealGemDao(context)

    fun write(methodDesc: String) {
        val time = System.currentTimeMillis()
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val processId = Utils.getProcessId().toLong()
        val threadName = currentThread.name
        val version = Utils.getVersionDesc(context)

        //过滤

        //写入
        executor.execute {
            dao.insertTraceLog(
                time,
                methodDesc,
                threadId,
                processId,
                threadName,
                version
            )
        }
    }
}