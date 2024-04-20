import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggerHilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.randos.musicvibe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.randos.musicvibe"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val properties = Properties()
    properties.load(rootProject.file("local.properties").inputStream())
    signingConfigs {
        create("release"){
            keyAlias = "${properties["keyAlias"]}"
            keyPassword = "${properties["keyPassword"]}"
            storeFile = file("keystore.jks")
            storePassword = "${properties["storePassword"]}"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- Dagger Hilt ---
    implementation(libs.hilt.android)

    // --- Live Data ---
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation (libs.androidx.runtime.livedata)

    // --- Navigation ---
    implementation(libs.androidx.hilt.navigation.compose)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Kapt ---
    kaptAndroidTest (libs.hilt.compiler)
    kaptTest (libs.hilt.compiler)
    kapt(libs.hilt.compiler)

    /*--------------- Local Test ----------------*/
    testImplementation(libs.junit)
    testImplementation(libs.hilt.android.testing)

    /*--------------- Instrumentation Tests ----------------*/
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
}

kapt {
    correctErrorTypes = true
}