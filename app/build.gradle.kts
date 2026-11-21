plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val resolvedTargetSdk = (project.findProperty("androidTargetSdk") as String?)?.toIntOrNull() ?: 37

android {
    namespace = "com.knigabogov.reader"
    compileSdk = resolvedTargetSdk

    defaultConfig {
        applicationId = "com.knigabogov.reader"
        minSdk = 26
        targetSdk = resolvedTargetSdk
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.webkit:webkit:1.13.0")
}
