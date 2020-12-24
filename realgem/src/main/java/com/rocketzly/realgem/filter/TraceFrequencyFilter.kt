package com.rocketzly.realgem.filter

import android.util.Log
import android.util.LruCache
import com.rocketzly.realgem.RealGem

/**
 * 日志频率过滤器
 * User: Rocket
 * Date: 2020/12/11
 * Time: 5:30 PM
 */
class TraceFrequencyFilter(private val realGem: RealGem) : TraceFilter {

    /**
     * 记录方法调用频率，只记录最近调用的方法
     * key为方法名，value为方法第一次调用时间|调用次数，例如：1607671035656|2
     */
    private val methodRecord = LruCache<String, String>(200)

    /**
     * 调用频繁的方法，需要过滤
     */
    private val frequentlyMethod = HashSet<String>()

    companion object {
        const val TAG = "TraceFrequencyFilter"

        const val SEPARATOR = "|"
    }

    override fun filter(
        time: Long,
        methodDesc: String,
        threadId: Long,
        processId: Long,
        threadName: String,
        version: String
    ): Boolean {
        synchronized(this) {
            if (frequentlyMethod.contains(methodDesc)) return true

            val record = methodRecord.get(methodDesc)
            if (record == null) {
                methodRecord.put(methodDesc, makeMethodRecord(time, 1))
                return false
            }

            val methodInfo = getMethodInfo(record)
            val firstCallTime = methodInfo[0].toLong()
            val count = methodInfo[1].toInt() + 1
            val frequency = (time - firstCallTime) / count.toFloat()
            if (frequency > realGem.frequencyThreshold && count > 10) {
                if (realGem.isDebug) {
                    Log.i(TAG, "addFrequentlyMethod:$methodDesc")
                }
                methodRecord.remove(methodDesc)
                frequentlyMethod.add(methodDesc)
                return true
            }
            methodRecord.put(methodDesc, makeMethodRecord(firstCallTime, count))

            return false
        }
    }

    private fun makeMethodRecord(time: Long, count: Int): String {
        return "$time$SEPARATOR$count"
    }

    private fun getMethodInfo(record: String): List<String> {
        return record.split(SEPARATOR)
    }
}