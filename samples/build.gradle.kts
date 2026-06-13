import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin.jvm)
  application
}

repositories {
  mavenCentral() 
}

dependencies {
  implementation(project(":adhan"))
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
    }
}

application {
 mainClass.set("com.batoulapps.adhan2.Example")
}
