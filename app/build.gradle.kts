import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    namespace = "com.cyb3rko.backpackdemo"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.cyb3rko.backpackdemo"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        signingConfig = signingConfigs.getByName("debug")
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.preference)
    implementation(project(":backpack"))
}
