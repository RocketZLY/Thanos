package com.rocketzly.realgem.db

import android.content.ContentValues
import android.content.Context

/**
 * User: Rocket
 * Date: 2020/12/4
 * Time: 3:57 PM
 */
class RealGemDao(val context: Context) {

    fun insertTraceLog(
        time: Long,
        method: String,
        threadId: Long,
        processId: Long,
        threadName: String,
        version: String
    ) {
        val values = ContentValues().apply {
            put(RealGemContract.TraceLogEntry.COLUMN_NAME_TIME, time)
            put(RealGemContract.TraceLogEntry.COLUMN_NAME_METHOD, method)
            put(RealGemContract.TraceLogEntry.COLUMN_NAME_THREAD_ID, threadId)
            put(RealGemContract.TraceLogEntry.COLUMN_NAME_PROCESS_ID, processId)
            put(RealGemContract.TraceLogEntry.COLUMN_NAME_THREAD_NAME, threadName)
            put(RealGemContract.TraceLogEntry.COLUMN_NAME_VERSION, version)
        }
        RealGemDbHelper.getDb(context)
            .insert(RealGemContract.TraceLogEntry.TABLE_NAME, null, values)
    }
}