plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggerHilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.randos.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":logger"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    // --- Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)


    // --- Dagger Hilt ---
    implementation(libs.hilt.android)

    // --- Media3 ExoPlayer
    implementation(libs.androidx.media3.exoplayer)

    // --- Data Store ---
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Kapt ---
    kaptAndroidTest (libs.hilt.compiler)
    kaptTest (libs.hilt.compiler)
    kapt(libs.hilt.compiler)
}

kapt {
    correctErrorTypes = true
}