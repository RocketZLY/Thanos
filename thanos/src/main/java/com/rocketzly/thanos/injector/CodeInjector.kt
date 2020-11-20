package com.rocketzly.thanos.injector

import javassist.ClassPool
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * User: Rocket
 * Date: 2020/11/20
 * Time: 5:27 PM
 */
class CodeInjector : Injector {

    override fun loadClassPath(
        dirFileList: List<File>,
        jarFileList: List<File>,
        androidJarFile: File
    ) {
        dirFileList.forEach {
            ClassPool.getDefault().insertClassPath(it.absolutePath)
        }
        jarFileList.forEach {
            ClassPool.getDefault().insertClassPath(it.absolutePath)
        }
        ClassPool.getDefault().insertClassPath(androidJarFile.absolutePath)
    }

    override fun inject(input: InputStream, out: OutputStream, qualifiedClassName: String) {
        ClassPool.getDefault().get(qualifiedClassName)
    }


}