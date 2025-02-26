# Stellarfi UI SDK

**Caution: Signed Internet Connection Required**

All calls using the Stellarfi UI SDK must implement a way to call and provide the users uuid. This is a mandatory security measure to prevent users from accessing unwarranted data support@stellarfi.com.

**Welcome to the Stellarfi Android UI SDK**

The Stellarfi Android UI SDK provides a pre-built, standardized user interface (UI) and user experience (UX) flow for onboarded users. It simplifies the integration of Stellarfi's web platform into your Android application.

This SDK allows you to quickly implement a user's credit experience by integrating it into your existing application or deploying it as a standalone app. Customizable UI/UX elements, such as fonts, themes, and enabled features, allow you to align the experience with your organization's branding.

**Note:** Advanced branding features, like card background images and UI element colors, require configuration changes on the Stellarfi backend. Contact us for more information.

This document outlines the following procedures:

* **Installation:** Installing the SDK.
* **Initialization:** Initializing the SDK.

**Note:** The UI SDK is made up of a series of fragments that need a Fragment View in your screens respective XML file

For detailed information on the Android Mobile SDK, refer to [Stellarfi Mobile SDK documentation link].

**Note:** The Stellarfi Mobile API enforces a request rate limit of 1 request per 30 seconds for verification and login endpoints.

**Requirements**

* Android SDK: Minimum API Level 24 (Marshmallow).
* Testing:
    * Android device running Android 6.0 (API Level 23) or higher.
    * Android emulator running Android 7.0 (API Level 24) or higher.
* Kotlin: Minimum version 1.5.21.
* Gradle: Minimum version 7.0.3.

**Note:** The SDK is developed in Kotlin but is fully interoperable with Java. Minor code adjustments may be necessary for Java projects.

The UI SDK incorporates the following Android permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

Obtaining the Mobile API Key

A Mobile API Key is essential for running the SDK. Refer to [Stellarfi Mobile API Key Retrieval Guide link] for instructions on obtaining your key.

Installation

Add Repositories: In your project's build.gradle file, include the following repositories:
    
**Gradle**

```
allprojects {
    repositories {
        // ... other repositories ...
        mavenCentral()
        maven { url "[https://jitpack.io](https://jitpack.io)" }
        // ... other repositories ...
    }
}
```

Configure Compile Options and Build Features: In your app's build.gradle file, add the following:
Gradle

```
android {
    // ... other configurations ...
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        dataBinding = true
    }
    // ... other configurations ...
}
```

Note: The compileOptions block is required if your minimum Android SDK version is below API Level 26 (Oreo).

Add Dependency: In your app's build.gradle file, add the UI SDK dependency:
Gradle
```
    dependencies {
        // ... other dependencies ...
        implementation 'com.stellarfi.sdk:ui:latest_version' //Replace latest_version with the most recent version.
        // ... other dependencies ...
    }
```


Initialization

The SDK must be initialized in the onCreate method of your Application class, using your Mobile API Key. Choose between one-step or two-step initialization.

One-Step Initialization (Recommended)

Initialize the SDK and provide the API key in the onCreate method.
Kotlin

import android.app.Application
import com.stellarfi.sdk.ui.StellarfiUiSdk
import com.stellarfi.sdk.core.StellarfiSdkEnvironment

class YourApp : Application() {
    override fun onCreate() {
        super.onCreate()
        StellarfiUiSdk.initializeWithApiKey(this, "YOUR_MOBILE_API_KEY") // production environment
        //Or for sandbox: StellarfiUiSdk.initializeWithApiKey(this, "YOUR_MOBILE_API_KEY", StellarfiSdkEnvironment.SBX)
        // ... other initialization ...
    }
}

Two-Step Initialization

Initialize the SDK without the API key, then set it later.
Kotlin

import android.app.Application
import com.stellarfi.sdk.ui.StellarfiUiSdk
import com.stellarfi.sdk.core.StellarfiSdkEnvironment

class YourApp : Application() {
    override fun onCreate() {
        super.onCreate()
        StellarfiUiSdk.initialize(this)
        // ... other initialization ...
    }
}

Set the API key before using any SDK methods:
