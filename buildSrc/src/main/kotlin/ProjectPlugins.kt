import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec

object Plugins {
    const val kotlinJvmId = "org.jetbrains.kotlin.jvm"
    const val ktLintId = "org.jlleitschuh.gradle.ktlint"
}

fun PluginDependenciesSpec.plugKotlin() = id(Plugins.kotlinJvmId) version Global.kotlin_version

fun PluginDependenciesSpec.plugSpringBoot() = id("org.springframework.boot") version LibVersion.spring_boot

fun PluginDependenciesSpec.plugSpringDependencyManagement() = id("io.spring.dependency-management") version "1.1.2"


fun PluginDependenciesSpec.plugKtLint() = id(Plugins.ktLintId) version "11.6.1"


fun PluginDependenciesSpec.plugSpringKotlin() = kotlin("plugin.spring") version Global.kotlin_version
