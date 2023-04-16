import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  application
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

repositories {
  mavenCentral() 
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation(project(":adhan"))
}

application {
 mainClass.set("com.batoulapps.adhan2.Example")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions { jvmTarget = "1.8" }