package com.rocketzly.thanos.transform

import com.rocketzly.thanos.ExtName
import com.rocketzly.thanos.extension.ThanosExt
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
        if (validClass(qualifiedClassName)) {
            return injector.inject(input, out, qualifiedClassName)
        }
        return false
    }

    /**
     * 是自己的包名，并过滤R.jar
     */
    private fun validClass(className: String): Boolean {
        val pkgName =
            (project.extensions.getByName(ExtName.THANOS) as? ThanosExt)?.packageName ?: return true

        return className.contains(pkgName) &&
                className != "$pkgName.R" &&
                !className.contains("$pkgName.R$")
    }

}