plugins {
    id("org.gradle.maven-publish")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
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
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    buildFeatures {
        compose = true
    }
}
afterEvaluate{
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.Stellar-Drew" // Changed to be JitPack compliant
                artifactId = "StellarfiScoreTile"
                version = "1.0.0" // You can change this to a commit hash later
            }
        }
        repositories {
            mavenLocal()
        }
    }
}


dependencies {
    implementation(libs.androidx.fragment)
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha02")
    implementation("androidx.compose.material:material-icons-core")

    implementation("androidx.compose.material:material-icons-extended")

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