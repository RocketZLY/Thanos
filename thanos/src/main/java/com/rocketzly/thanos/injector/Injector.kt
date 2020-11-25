package com.rocketzly.thanos.injector

import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * User: Rocket
 * Date: 2020/11/20
 * Time: 7:20 PM
 */
interface Injector {

    /**
     * 加载classPath
     */
    fun loadClassPath(dirFileList: List<File>, jarFileList: List<File>, androidJarFile: File)

    /**
     * 插入代码
     * @return 返回false的话则交给BaseTransform默认处理即复制原文件、否则自己处理
     */
    fun inject(input: InputStream, out: OutputStream, qualifiedClassName: String): Boolean
}