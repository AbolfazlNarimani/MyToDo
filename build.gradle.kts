// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.3")

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")

        classpath("androidx.test:runner:1.5.2@aar")
        // NOTE: Do not place your application dependencies here; they belong

        // in the individual module build.gradle files
    }
}

plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}