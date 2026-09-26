plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.comprasegura"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.comprasegura"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // 10.0.2.2 es el "localhost" de la PC vista desde el emulador.
            // En dispositivo fisico, cambiar por la IP de la PC en la red local.
            buildConfigField("String", "API_URL", "\"http://10.0.2.2:8000/api/v1/\"")
        }
        release {
            buildConfigField("String", "API_URL", "\"https://CAMBIA_ESTE_DOMINIO/api/v1/\"")
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
