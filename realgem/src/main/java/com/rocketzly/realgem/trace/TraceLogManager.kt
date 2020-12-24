package com.rocketzly.realgem.trace

import com.rocketzly.realgem.RealGem
import com.rocketzly.realgem.Utils
import com.rocketzly.realgem.db.RealGemDao
import com.rocketzly.realgem.filter.TraceFrequencyFilter
import java.util.concurrent.Executors

/**
 * 负责日志管理
 * User: Rocket
 * Date: 2020/12/4
 * Time: 2:25 PM
 */
class TraceLogManager(private val realGem: RealGem) {

    companion object {
        private val executor = Executors.newSingleThreadExecutor {
            Thread(it, "realGem")
        }
    }

    private val dao = RealGemDao(realGem.context)
    private val filter = TraceFrequencyFilter(realGem)

    fun write(methodDesc: String) {
        val time = System.currentTimeMillis()
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val processId = Utils.getProcessId().toLong()
        val threadName = currentThread.name
        val version = Utils.getVersionDesc(realGem.context)

        //过滤
        if (filter.filter(time, methodDesc, threadId, processId, threadName, version))
            return

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