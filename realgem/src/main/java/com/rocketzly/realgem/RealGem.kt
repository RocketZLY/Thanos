package com.rocketzly.realgem

import android.content.Context
import com.rocketzly.realgem.trace.TraceLogManager

/**
 * 日志操作者，不包含具体逻辑，只组装流程
 * User: Rocket
 * Date: 2020/11/19
 * Time: 4:20 PM
 */
class RealGem private constructor() {

    private var init = false
    private lateinit var context: Context
    private lateinit var logManager: TraceLogManager

    companion object {

        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RealGem()
        }
    }

    @Synchronized
    fun init(context: Context) {
        if (init) return
        init = true

        this.context = context
        this.logManager = TraceLogManager(context)
    }

    fun use(methodDesc: String) {
        if (!init) return
        //写入
        logManager?.write(methodDesc)
    }


}