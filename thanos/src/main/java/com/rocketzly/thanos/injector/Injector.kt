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

    fun loadClassPath(dirFileList: List<File>, jarFileList: List<File>, androidJarFile: File)

    fun inject(input: InputStream, out: OutputStream, qualifiedClassName: String): Boolean
}