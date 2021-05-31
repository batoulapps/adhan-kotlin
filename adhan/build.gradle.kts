plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.5.10"
    id("maven-publish")
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
        api("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
        api("com.squareup.okio:okio-multiplatform:3.0.0-alpha.5")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
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