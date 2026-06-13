@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.maven.publish)
}

group = "com.batoulapps.adhan"
version = property("version") ?: ""

kotlin {
    jvm()

    js { nodejs {} }

    wasmJs { nodejs {} }

    linuxX64()
    linuxArm64()
    mingwX64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosArm64()
    macosX64()

    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            api(libs.okio)
            implementation(libs.kotlinx.serialization.json)
        }

        jvmTest.dependencies { }

        jsTest.dependencies {
            implementation(libs.okio.nodefilesystem)
            implementation(npm("@js-joda/timezone", libs.versions.jsJodaTimezone.get()))
        }

        wasmJsTest.dependencies {
            implementation(npm("@js-joda/timezone", libs.versions.jsJodaTimezone.get()))
        }
    }

    // set an environment variable and read it in the test
    // https://publicobject.com/2023/04/16/read-a-project-file-in-a-kotlin-multiplatform-test/
    tasks.withType<KotlinJvmTest>().configureEach {
        environment("ADHAN_ROOT", rootDir)
    }

    tasks.withType<KotlinNativeTest>().configureEach {
        // required for the variable to propagate to the simulator
        environment("SIMCTL_CHILD_ADHAN_ROOT", rootDir)
        environment("ADHAN_ROOT", rootDir)
    }

    tasks.withType<KotlinJsTest>().configureEach {
        environment("ADHAN_ROOT", rootDir.toString())
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("com.batoulapps.adhan", "adhan2", version.toString())

    pom {
        name.set("Adhan Prayertimes Library")
        description.set("A high precision Islamic prayer times library")
        url.set("https://github.com/batoulapps/adhan-kotlin")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
            }
        }
        scm {
            url.set("https://github.com/batoulapps/adhan-kotlin")
        }

    }
}

// auto replace yarn.lock
rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin::class.java) {
    rootProject.the<YarnRootExtension>().yarnLockMismatchReport =
        YarnLockMismatchReport.WARNING
    rootProject.the<YarnRootExtension>().yarnLockAutoReplace = true
}
