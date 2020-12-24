package com.rocketzly.thanos.injector

import com.rocketzly.thanos.Constants
import com.rocketzly.thanos.extension.ThanosExt
import javassist.CtMethod
import javassist.bytecode.AccessFlag
import org.apache.commons.io.FilenameUtils

/**
 * User: Rocket
 * Date: 2020/11/25
 * Time: 3:19 PM
 */
class InjectFilter {

    /**
     * 有效的类，过滤掉如下几类
     * 1. 先排除R文件
     * 2. 过滤掉realGem本身
     * 3. 根据[com.rocketzly.thanos.extension.ThanosExt.isFull]判断是全量扫描，还是只扫包名下的类，默认只扫包名下的类
     * 4. 在排除 [com.rocketzly.thanos.extension.ThanosExt.exclude]中的类
     */
    fun validClass(
        qualifiedClassName: String,
        pkgName: String,
        thanosExt: ThanosExt
    ): Boolean {
        if (isR(qualifiedClassName, pkgName)) return false
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
    private fun isR(qualifiedClassName: String, pkgName: String): Boolean {
        return qualifiedClassName == "$pkgName.R" || qualifiedClassName.contains("$pkgName.R$")
    }

    /**
     * 有效的方法，过滤掉如下几类
     * 1. 抽象方法
     * 2. native方法
     * 3. synthetic方法
     * 4. kt自动生成方法（_$_findCachedViewById(int),_$_clearFindViewByIdCache()）
     */
    fun validMethod(ctMethod: CtMethod): Boolean {
        if (ctMethod.isEmpty) return false//抽象方法
        if (ctMethod.modifiers and AccessFlag.NATIVE != 0) return false//native方法
        if (ctMethod.modifiers and AccessFlag.SYNTHETIC != 0) return false//synthetic方法
        val longName = ctMethod.longName
        if (longName.contains("_\$_findCachedViewById(int)") ||
            longName.contains("_\$_clearFindViewByIdCache()")
        ) return false//kt自动生成方法
        return true
    }
}