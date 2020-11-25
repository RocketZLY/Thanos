package com.rocketzly.thanos

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.rocketzly.thanos.extension.ThanosExt
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/11/25
 * Time: 5:06 PM
 */
class Utils {

    companion object {

        fun getThanosExt(project: Project): ThanosExt {
            return project.extensions.getByName(Constants.EXT_NAME_THANOS) as ThanosExt
        }

        @Suppress("UnstableApiUsage")
        fun getPkgName(project: Project): String {
            val androidExtension = project.extensions.getByName("android") as AppExtension
            val startParam = project.gradle.startParameter.taskRequests.toString()

            val regex = Regex("assemble(\\w*)(Release|Debug)")
            val taskName = regex.find(startParam)?.value!!
            val variantName = taskName.substring("assemble".length)

            return (androidExtension.applicationVariants.find { variant ->
                variant.name.capitalize() == variantName
            } as ApplicationVariantImpl).applicationId
        }
    }
}