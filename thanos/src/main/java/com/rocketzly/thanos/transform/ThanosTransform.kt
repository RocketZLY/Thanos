package com.rocketzly.thanos.transform

import com.rocketzly.thanos.injector.CodeInjector
import javassist.ClassPool
import org.gradle.api.Project
import java.io.DataOutputStream
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
        if (qualifiedClassName.contains(PKG_NAME) && validClass(qualifiedClassName)) {
            injector.inject(input, out, qualifiedClassName)
            return true
        }
        return false
    }

    companion object {
        const val PKG_NAME = "com.rocketzly"
    }

    /**
     * 过滤R.jar
     */
    fun validClass(className: String): Boolean =
        className != "$PKG_NAME.R" && !className.contains("$PKG_NAME.R$")

}