import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  application
}

repositories {
  mavenCentral() 
}

dependencies {
  implementation(project(":adhan"))
}

application {
 mainClass.set("com.batoulapps.adhan2.Example")
}