buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
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