import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.7.10"
    id("maven-publish")
    id("signing")
}

group = "com.batoulapps.adhan"
version = property("version") ?: ""

kotlin {
    jvm()

    js(IR) {
       useCommonJs()
       browser()
    }

    linuxX64("linux")

    ios()
    iosSimulatorArm64()

    macosArm64("macOS")
    macosX64("macOSX64")

    watchos()
    watchosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                api("com.squareup.okio:okio:3.2.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
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
                implementation("com.squareup.okio:okio-nodefilesystem:3.2.0")
            }
        }

        val appleTest by creating { dependsOn(commonTest) }
        val iosTest by getting { dependsOn(appleTest) }
        val watchosTest by getting { dependsOn(appleTest) }

        sourceSets["macOSTest"].dependsOn(appleTest)
        sourceSets["macOSX64Test"].dependsOn(appleTest)
        sourceSets["iosSimulatorArm64Test"].dependsOn(appleTest)
        sourceSets["watchosSimulatorArm64Test"].dependsOn(appleTest)
    }
}

// taken from here with minor modifications:
// https://dev.to/kotlin/how-to-build-and-publish-a-kotlin-multiplatform-library-going-public-4a8k
// planning on moving this to a plugin one day.

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = project.property("signing.keyId")
ext["signing.password"] = project.property("signing.password")
ext["signing.secretKeyRingFile"] = project.property("signing.secretKeyRingFile")
ext["ossrhUsername"] = project.property("mavenCentralRepositoryUsername")
ext["ossrhPassword"] = project.property("mavenCentralRepositoryPassword")

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

// Signing artifacts. Signing.* extra properties values will be used
signing {
    sign(publishing.publications)
}
