// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
buildscript {
    val kotlin_version = "1.9.0"


    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.4")

        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

    }
}



