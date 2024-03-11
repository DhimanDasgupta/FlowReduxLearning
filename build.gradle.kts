// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.hilt) apply false
    kotlin("jvm") version libs.versions.kotlin apply false // Match Kotlin version
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
}