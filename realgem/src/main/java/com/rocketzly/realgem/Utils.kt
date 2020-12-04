package com.rocketzly.realgem

import android.content.Context

/**
 * User: Rocket
 * Date: 2020/12/4
 * Time: 11:04 AM
 */
class Utils {

    companion object {
        var pId = 0
        var versionDesc = ""

        fun getProcessId(): Int {
            if (pId != 0) return pId
            pId = android.os.Process.myPid()
            return pId
        }

        fun getVersionDesc(context: Context): String {
            if (versionDesc.isNotEmpty()) return versionDesc
            val pm = context.packageManager
            val info = pm.getPackageInfo(context.packageName, 0)
            versionDesc = "${info.versionCode}|${info.versionName}"
            return versionDesc
        }
    }
}