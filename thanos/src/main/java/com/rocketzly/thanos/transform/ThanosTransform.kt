package com.rocketzly.thanos.transform

import com.rocketzly.thanos.injector.CodeInjector
import org.gradle.api.Project
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * User: Rocket
 * Date: 2020/11/11
 * Time: 5:15 PM
 */
class ThanosTransform(project: Project) : BaseTransform(project) {

    private val injector = CodeInjector()
    private val filter = ClassFilter(project)

    override fun getName(): String {
        return ThanosTransform::class.java.simpleName
    }

    override fun loadClassPath(
        dirFileList: List<File>,
        jarFileList: List<File>,
        androidJarFile: File
    ) {
        injector.loadClassPath(dirFileList, jarFileList, androidJarFile)
    }

    override fun apply(input: InputStream, out: OutputStream, qualifiedClassName: String): Boolean {
        if (filter.validClass(qualifiedClassName)) {
            return injector.inject(input, out, qualifiedClassName)
        }
        return false
    }
}