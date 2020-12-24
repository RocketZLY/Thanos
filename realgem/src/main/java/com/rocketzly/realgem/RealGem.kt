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

    internal var isDebug = false

    /**
     * 方法频率阈值
     */
    internal var frequencyThreshold = DEF_FREQUENCY_THRESHOLD
    internal lateinit var context: Context
    internal lateinit var logManager: TraceLogManager

    companion object {

        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RealGem()
        }

        /**
         * 默认方法频率阈值，单位：毫秒/次数，超过则认为是频繁调用的方法
         */
        const val DEF_FREQUENCY_THRESHOLD = 1000 / 60f
    }

    fun init(context: Context) {
        this.context = context
        this.logManager = TraceLogManager(this)
    }

    fun debug(isDebug: Boolean): RealGem {
        this.isDebug = isDebug
        return this
    }

    fun methodFrequencyThreshold(threshold: Float): RealGem {
        this.frequencyThreshold = threshold
        return this
    }

    fun use(methodDesc: String) {
        //写入
        if (this::logManager.isInitialized) {
            logManager.write(methodDesc)
        }
    }

}