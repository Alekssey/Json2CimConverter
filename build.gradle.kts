plugins {
    plugKotlin()
    plugKtLint()
    application
}

repositories {
    mavenCentral()
    mavenLocal()
}

subprojects {
    repositories {
        mavenCentral()
        maven {
            name = "DTPS-GitLab-MPEI"
            url = uri("https://gitlab.rza-mpei.ru/api/v4/groups/10/-/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = if (getJobToken() != null) "Job-Token" else "Private-Token"
                value = getAccessToken(
                    "CI_GITLAB_RZA_ACCESS_TOKEN",
                    "gitlab-rza-access-token"
                )
            }
            authentication {
                create("header", HttpHeaderAuthentication::class)
            }
        }
        mavenLocal()
    }

    apply {
        plugin("java")
        plugin(Plugins.kotlinJvmId)
        plugin(Plugins.ktLintId)
    }

    configurations.all {
        resolutionStrategy {
            eachDependency {
                requested.version?.contains("snapshot", true)?.let {
                    if (it) {
                        throw GradleException("Snapshot found: ${requested.name} ${requested.version}")
                    }
                }
            }
        }
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
                freeCompilerArgs = listOf("-Xjvm-default=all")
            }
        }

        withType<JavaCompile> {
            options.compilerArgs.add("-Xlint:all")
        }

        withType<Test> {
            useJUnitPlatform()
        }
    }
}

fun getAccessToken(environmentVariableName: String, gradlePropertyName: String): String {
    return getJobToken()
        ?: System.getenv(environmentVariableName)
        ?: findProperty(gradlePropertyName) as String?
        ?: throw NoSuchElementException(
            "There is no environment variable \"$environmentVariableName\" " +
                "and there is no property \"$gradlePropertyName\" in gradle.properties file"
        )
}

task("installGitHook", Copy::class) {
    from(File(rootProject.rootDir, "tools/git-hooks/pre-commit"))
    into { File(rootProject.rootDir, ".git/hooks") }
    fileMode = 777
}

tasks.getByPath("build").dependsOn("installGitHook")

fun getJobToken() = System.getenv("CI_JOB_TOKEN")
