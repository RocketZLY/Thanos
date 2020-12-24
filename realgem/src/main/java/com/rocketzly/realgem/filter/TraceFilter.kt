package com.rocketzly.realgem.filter

/**
 * User: Rocket
 * Date: 2020/12/11
 * Time: 5:32 PM
 */
interface TraceFilter {

    /**
     * @return true则为过滤掉该方法
     */
    fun filter(
        time: Long,
        methodDesc: String,
        threadId: Long,
        processId: Long,
        threadName: String,
        version: String
    ): Boolean
}