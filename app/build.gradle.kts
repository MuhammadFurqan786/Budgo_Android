plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleServices)
    id("kotlin-parcelize")

}

android {
    namespace = "com.sokoldev.budgo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sokoldev.budgo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.google.map.utils)
    implementation(libs.com.github.chinalwb)
    implementation(libs.com.github.philjay)
    implementation(libs.com.google.gson)
    implementation(libs.com.squareup.retrofit2.retrofit)
    implementation(libs.com.squareup.retrofit2.retrofit.converter)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.com.squareup.okhttp3)
<<<<<<< HEAD
    implementation(libs.logging.interceptor)
=======
    implementation(libs.com.logging.interceptor)
>>>>>>> a4e69efb6fa23fe919b87e392714ea54b84705d4
    implementation(libs.com.github.bumptec.glide)
    implementation(libs.pinview)
    //room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.firebase.database.ktx)
    implementation(libs.play.services.location)
    ksp(libs.androidx.room.compiler)

    //paging
    implementation(libs.paging)

    // Stripe
    implementation(libs.stripe)
    // circle image view
    implementation(libs.circleimageview)


    // CameraX
    implementation(libs.androidx.camera2)
    implementation(libs.androidx.camera2.lifecycle)
    implementation(libs.androidx.camera2.view)

    // ML kit Text Recoginition
    implementation(libs.ml.kit.text.recognition)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}