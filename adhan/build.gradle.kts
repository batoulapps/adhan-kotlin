plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.5.21"
    id("maven-publish")
    id("signing")
}

group = "com.batoulapps.adhan"
version = property("version") ?: ""

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        val test by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }
    macosX64("macOS")

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        api("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
        api("com.squareup.okio:okio-multiplatform:3.0.0-alpha.9")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    }

    sourceSets["jvmTest"].dependencies {
        dependencies {
            implementation(kotlin("test-junit"))
            implementation("junit:junit:4.13.2")
            implementation(kotlin("stdlib-jdk8"))
        }
    }

    sourceSets["iosTest"].dependencies {  }
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
