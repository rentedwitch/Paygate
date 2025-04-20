plugins {
    alias(libs.plugins.android.application)  // Keep this line for Android app plugin
    alias(libs.plugins.kotlin.android) // Kotlin plugin
    id("com.google.gms.google-services") // Google Services for Firebase
}

android {
    namespace = "com.example.paygate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.paygate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"  // Ensure you have the custom ProGuard rules here
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx) // AndroidX Core library
    implementation(libs.androidx.appcompat) // AndroidX AppCompat library
    implementation(libs.material) // Material Components
    implementation(libs.androidx.activity) // AndroidX Activity
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore) // ConstraintLayout

    // Testing dependencies
    testImplementation(libs.junit) // JUnit for unit tests
    androidTestImplementation(libs.androidx.junit) // AndroidJUnitRunner for instrumentation tests
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing

    // Firebase dependencies
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation(platform("com.google.firebase:firebase-bom:33.12.0")) // Firebase BOM for version management
    implementation("com.google.firebase:firebase-auth") // Firebase Authentication

    // ZXing (QR Scanner) dependencies
    implementation("com.journeyapps:zxing-android-embedded:4.1.0") // ZXing Android Embedded
    implementation("com.google.zxing:core:3.3.0") // ZXing core library
}
