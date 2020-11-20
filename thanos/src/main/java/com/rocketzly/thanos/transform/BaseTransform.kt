package com.rocketzly.thanos.transform

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.builder.utils.isValidZipEntryName
import com.android.utils.FileUtils
import com.google.common.io.Files
import org.gradle.api.Project
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * User: Rocket
 * Date: 2020/10/26
 * Time: 10:52 AM
 */
abstract class BaseTransform(val project: Project) : Transform() {

    override fun getInputTypes(): Set<QualifiedContent.ContentType?> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    @Throws(InterruptedException::class, IOException::class)
    override fun transform(invocation: TransformInvocation) {
        val outputProvider = invocation.outputProvider!!

        if (!invocation.isIncremental) {
            outputProvider.deleteAll()
        }

        val dirFileList = mutableListOf<File>()
        val jarFileList = mutableListOf<File>()
        invocation.inputs.forEach { input ->
            dirFileList.addAll(input.directoryInputs.map {
                it.file
            })
            jarFileList.addAll(input.jarInputs.map {
                it.file
            })
        }
        loadClassPath(dirFileList, jarFileList, getAndroidJarFile())

        for (ti in invocation.inputs) {
            for (jarInput in ti.jarInputs) {
                val inputJar = jarInput.file
                val outputJar = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                if (invocation.isIncremental) {
                    when (jarInput.status) {
                        Status.NOTCHANGED -> {
                        }
                        Status.ADDED, Status.CHANGED -> transformJar(
                            inputJar,
                            outputJar
                        )
                        Status.REMOVED -> FileUtils.delete(
                            outputJar
                        )
                    }
                } else {
                    transformJar(inputJar, outputJar)
                }
            }
            for (di in ti.directoryInputs) {
                val inputDir = di.file
                val outputDir = outputProvider.getContentLocation(
                    di.name,
                    di.contentTypes,
                    di.scopes,
                    Format.DIRECTORY
                )
                if (invocation.isIncremental) {
                    for ((inputFile, value) in di.changedFiles) {
                        when (value) {
                            Status.NOTCHANGED -> {
                            }
                            Status.ADDED, Status.CHANGED -> {
                                transformFile(inputFile, inputDir, outputDir)
                            }
                            Status.REMOVED -> {
                                val outputFile =
                                    toOutputFile(outputDir, inputDir, inputFile)
                                FileUtils.deleteIfExists(outputFile)
                            }
                        }
                    }
                } else {
                    for (inputFile in FileUtils.getAllFiles(inputDir)) {
                        transformFile(inputFile, inputDir, outputDir)
                    }
                }
            }
        }

    }

    @Throws(IOException::class)
    private fun transformJar(
        inputJar: File,
        outputJar: File
    ) {
        Files.createParentDirs(outputJar)
        val zipFile = ZipFile(inputJar)
        val fos = FileOutputStream(outputJar)
        val zos = ZipOutputStream(fos)

        val entries = zipFile.entries()
        var inputEntry: ZipEntry
        while (entries.hasMoreElements()) {
            inputEntry = entries.nextElement()
            if (inputEntry.isDirectory) {//目录跳过
                continue
            }

            val zis = zipFile.getInputStream(inputEntry)
            val outputEntry = ZipEntry(inputEntry.name)
            zos.putNextEntry(outputEntry)

            if (!inputEntry.name.endsWith(SdkConstants.DOT_CLASS)) {//非class直接拷贝
                copy(zis, zos)
                zis.close()
                continue
            }

            //class交给apply处理
            if (!apply(
                    zis,
                    zos,
                    classPathToName(inputEntry.name)
                )
            ) {//未处理的话直接拷贝
                val temp = zipFile.getInputStream(inputEntry)//重新生成一个流避免apply操作过zis的影响
                copy(temp, zos)
                temp.close()
            }
            zis.close()
        }
        zos.close()
    }


    @Throws(IOException::class)
    private fun transformFile(
        inputFile: File,
        inputDir: File,
        outputDir: File
    ) {
        if (inputFile.isDirectory) {//目录不处理
            return
        }

        val outputFile = toOutputFile(outputDir, inputDir, inputFile)
        Files.createParentDirs(outputFile)
        val fis = BufferedInputStream(FileInputStream(inputFile))
        val fos = FileOutputStream(outputFile)

        if (!inputFile.name.endsWith(SdkConstants.DOT_CLASS)) {//非class文件直接copy
            copy(fis, fos)
            fis.close()
            fos.close()
            return
        }

        fis.mark(0)
        //class文件交给apply方法处理
        if (!apply(fis, fos, getQualifiedClassName(inputFile, inputDir))) {//没有处理的话直接拷贝
            fis.reset()
            copy(fis, fos)
        }
        fis.close()
        fos.close()
    }

    private fun toOutputFile(
        outputDir: File,
        inputDir: File,
        inputFile: File
    ): File {
        return File(
            outputDir,
            FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir)
        )
    }

    /**
     * 获取file全类名
     */
    private fun getQualifiedClassName(file: File, dirFile: File): String {
        return classPathToName(FileUtils.relativePossiblyNonExistingPath(file, dirFile))
    }

    private fun classPathToName(path: String): String {
        return path.removeSuffix(SdkConstants.DOT_CLASS).replace("/", ".")
    }

    fun copy(
        input: InputStream,
        out: OutputStream
    ) {
        var buffer = input.read()
        while (buffer != -1) {
            out.write(buffer)
            buffer = input.read()
        }
    }

    fun getAndroidJarFile(): File {
        val ext = project.extensions.getByType(BaseExtension::class.java)
        return File(ext.sdkDirectory, "platforms/${ext.compileSdkVersion}/android.jar")
    }

    /**
     * return false的话直接拷贝该文件，true的话则交给自己处理
     */
    abstract fun apply(
        input: InputStream,
        out: OutputStream,
        qualifiedClassName: String
    ): Boolean

    /**
     * 加载classPath
     */
    open fun loadClassPath(dirFileList: List<File>, jarFileList: List<File>, androidJarFile: File) {

    }
}