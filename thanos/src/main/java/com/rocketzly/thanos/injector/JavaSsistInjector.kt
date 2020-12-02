package com.rocketzly.thanos.injector

import com.rocketzly.thanos.Constants
import com.rocketzly.thanos.Utils
import javassist.ClassPool
import org.gradle.api.Project
import java.io.DataOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * User: Rocket
 * Date: 2020/11/20
 * Time: 5:27 PM
 */
class JavaSsistInjector(project: Project) : Injector {

    val pkgName by lazy {
        Utils.getPkgName(project)
    }
    val thanosExt by lazy {
        Utils.getThanosExt(project)
    }
    val filter = InjectFilter()

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

        ClassPool.getDefault().importPackage(Constants.PKG_NAME_REALGEM)//插入realgem
    }

    override fun inject(
        input: InputStream,
        out: OutputStream,
        qualifiedClassName: String
    ): Boolean {
        if (!filter.validClass(qualifiedClassName, pkgName, thanosExt)) return false

        val ctClass = ClassPool.getDefault().get(qualifiedClassName)
        ctClass.declaredMethods.forEach {
            if (!filter.validMethod(it)) return@forEach//过滤无效的方法

            println("methodName:${it.longName}")
            it.insertBefore("RealGem.getInstance().use(\"${it.longName}\");")
            it.insertAfter("RealGem.getInstance().use(\"${it.longName}\");")

        }
        ctClass.classFile.write(DataOutputStream(out))
        ctClass.detach()

        return true
    }


}