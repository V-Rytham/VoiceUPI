import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")

}


val secretsProperties = Properties()
val secretsFile = rootProject.file("secrets.properties")
if (secretsFile.exists()) {
    secretsProperties.load(FileInputStream(secretsFile))
}


android {
    namespace = "com.example.voiceupi"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.voiceupi"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Add this buildConfigField
        buildConfigField("String", "GEMINI_API_KEY", "\"${secretsProperties["GEMINI_API_KEY"]}\"")

    }
    // Add this inside android block
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.google.android.material:material:1.9.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0") // ZXing library
    implementation("com.google.zxing:core:3.4.1")// ZXing core


}