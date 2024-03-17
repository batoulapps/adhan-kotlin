import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.22"
    id("maven-publish")
    id("signing")
}

group = "com.batoulapps.adhan"
version = property("version") ?: ""

kotlin {
    jvm()

    js(IR) {
        nodejs {
            testTask {
                useMocha {
                    timeout = "30s"
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs {
            testTask {
                useMocha {
                    timeout = "30s"
                }
            }
        }
    }

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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                api("com.squareup.okio:okio:3.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("com.squareup.okio:okio-nodefilesystem:3.8.0")
                implementation(npm("@js-joda/timezone", "2.3.0"))
            }
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(npm("@js-joda/timezone", "2.3.0"))
            }
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

// Fix Gradle warning about signing tasks using publishing task outputs without explicit dependencies
// https://github.com/gradle/gradle/issues/26091
tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}

// taken from here with minor modifications:
// https://dev.to/kotlin/how-to-build-and-publish-a-kotlin-multiplatform-library-going-public-4a8k
// planning on moving this to a plugin one day.

// Stub secrets to let the project sync and build without the publication values set up
fun propertyOrEmpty(name: String): String {
    return if (project.hasProperty(name)) {
        project.property(name).toString()
    } else {
        ""
    }
}

ext["signing.keyId"] = propertyOrEmpty("signing.keyId")
ext["signing.password"] = propertyOrEmpty("signing.password")
ext["signing.secretKeyRingFile"] = propertyOrEmpty("signing.secretKeyRingFile")
ext["ossrhUsername"] = propertyOrEmpty("mavenCentralRepositoryUsername")
ext["ossrhPassword"] = propertyOrEmpty("mavenCentralRepositoryPassword")

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    publications.withType<MavenPublication> {
        artifactId = artifactId.replace("adhan", "adhan2")
    }

    // Configure maven central repository
    repositories {
        maven {
            name = "snapshots"
            setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
            mavenContent {
                snapshotsOnly()
            }
        }

        maven {
            name = "sonatype"
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
            mavenContent {
                releasesOnly()
            }
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("Adhan Prayertimes Library")
            description.set("A high precision Islamic prayer times library")
            url.set("https://github.com/batoulapps/adhan-java")

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
                url.set("https://github.com/batoulapps/adhan-java")
            }

        }
    }
}

// for wasm to be able to run tests
with(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.apply(rootProject)) {
    nodeVersion = "21.0.0-v8-canary202309167e82ab1fa2"
    nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
}

// auto replace yarn.lock
rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin::class.java) {
    rootProject.the<YarnRootExtension>().yarnLockMismatchReport =
        YarnLockMismatchReport.WARNING
    rootProject.the<YarnRootExtension>().yarnLockAutoReplace = true
}

// Signing artifacts. Signing.* extra properties values will be used
signing {
    sign(publishing.publications)
}
