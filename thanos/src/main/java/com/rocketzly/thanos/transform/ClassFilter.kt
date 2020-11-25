package com.rocketzly.thanos.transform

import com.rocketzly.thanos.Constants
import com.rocketzly.thanos.Utils
import org.apache.commons.io.FilenameUtils
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/11/25
 * Time: 3:19 PM
 */
class ClassFilter(private val project: Project) {

    private val pkgName by lazy {
        Utils.getPkgName(project)
    }

    private val thanosExt by lazy {
        Utils.getThanosExt(project)
    }

    /**
     * 1. 先排除R文件
     * 2. 过滤掉realGem本身
     * 3. 根据[com.rocketzly.thanos.extension.ThanosExt.isFull]判断是全量扫描，还是只扫包名下的类
     * 4. 在排除 [com.rocketzly.thanos.extension.ThanosExt.exclude]中的类
     */
    fun validClass(qualifiedClassName: String): Boolean {
        if (isR(qualifiedClassName)) return false
        if (qualifiedClassName.contains(Constants.PKG_NAME_REALGEM)) return false//过滤realgem
        if (!thanosExt.isFull && !qualifiedClassName.contains(pkgName)) return false
        if (thanosExt.exclude.find {
                FilenameUtils.wildcardMatch(
                    qualifiedClassName,
                    it
                )
            } != null) return false
        return true
    }

    /**
     * 过滤R文件
     */
    private fun isR(qualifiedClassName: String): Boolean {
        return qualifiedClassName == "$pkgName.R" || qualifiedClassName.contains("$pkgName.R$")
    }
}