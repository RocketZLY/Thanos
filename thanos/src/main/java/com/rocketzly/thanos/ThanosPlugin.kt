package com.rocketzly.thanos

import com.android.build.gradle.AppExtension
import com.rocketzly.thanos.transform.ThanosTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/10/13
 * Time: 11:30 AM
 */
class ThanosPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // 获取Android扩展
        val android = project.extensions.getByType(AppExtension::class.java)
        // 注册Transform，其实就是添加了Task
        android.registerTransform(
            ThanosTransform(project)
        )

    }
}