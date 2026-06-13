plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

// Disable NPM to NodeJS nightly compatibility check.
// Drop this when NodeJs version that supports latest Wasm become stable
// Required so wasm tests can run
tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
    args.add("--ignore-engines")
}
