package com.rocketzly.realgem.db

/**
 * User: Rocket
 * Date: 2020/12/4
 * Time: 3:31 PM
 */
class RealGemContract {


    object TraceLogEntry {
        const val TABLE_NAME = "traceLog"
        const val COLUMN_NAME_ID = "_id"
        const val COLUMN_NAME_TIME = "time"
        const val COLUMN_NAME_METHOD = "method"
        const val COLUMN_NAME_THREAD_ID = "threadId"
        const val COLUMN_NAME_PROCESS_ID = "processId"
        const val COLUMN_NAME_THREAD_NAME = "threadName"
        const val COLUMN_NAME_VERSION = "version"
    }
}