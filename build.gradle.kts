import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    kotlin("plugin.serialization") version "1.8.21"
}

android {
    namespace = "com.stellarfi.widget"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Updated to 1.5.3
    }
    buildFeatures {
        compose = true
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.stellarfi"
                artifactId = "StellarfiScoreTile"
                version = "1.0.0"
            }
        }
    }
}

tasks.register<Wrapper>("wrapper") {
    gradleVersion = "8.2."
}

dependencies {
    implementation(libs.androidx.fragment)
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation( "com.android.tools.build:gradle:4.0.0")
    implementation("com.github.dcendents:android-maven-gradle-plugin:2.1")
    implementation("com.google.guava:guava:29.0-jre")
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material:material-icons-core")

    implementation("androidx.compose.material:material-icons-extended")

    implementation ("com.github.jitpack:gradle-simple:1.1")

    implementation("androidx.compose.material3.adaptive:adaptive:1.0.0")


    implementation("androidx.activity:activity-compose")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

    implementation("androidx.compose.runtime:runtime-livedata")

    implementation("androidx.compose.runtime:runtime-rxjava2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}