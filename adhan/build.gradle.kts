plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.10"
}

kotlin {
    jvm()
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
        api("com.squareup.okio:okio-multiplatform:3.0.0-alpha.1")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    }

    sourceSets["jvmTest"].dependencies {
        dependencies {
            implementation(kotlin("test-junit"))
            implementation("junit:junit:4.12")
            implementation(kotlin("stdlib-jdk8"))
        }
    }

    sourceSets["iosTest"].dependencies {  }
}
