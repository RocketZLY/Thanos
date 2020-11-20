package com.rocketzly

import android.app.Application
import com.rocketzly.realgem.RealGem

/**
 * User: Rocket
 * Date: 2020/11/20
 * Time: 3:59 PM
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RealGem.instance.init(this)
    }
}