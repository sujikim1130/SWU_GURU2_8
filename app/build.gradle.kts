<<<<<<< HEAD
import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

=======
>>>>>>> main
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
<<<<<<< HEAD
    namespace = "com.example.financialledgerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.financialledgerapp"
        minSdk = 24
        targetSdk = 34
=======
    namespace = "com.example.guru_8"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.guru_8"
        minSdk = 24
        targetSdk = 35
>>>>>>> main
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
<<<<<<< HEAD

}

dependencies {
=======
//xml과 앱의 데이터 소스를 직접 연결하기 위해 추가
    buildFeatures {
        viewBinding = true
        }
    }



dependencies {
    val fragment_version = "1.8.3"
    // Java language implementation
    implementation("androidx.fragment:fragment:$fragment_version")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
>>>>>>> main
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======

    implementation(libs.mpandroidchart) //차트
>>>>>>> 29cfd2a4639170523299994044e5e6bafa70b24e
>>>>>>> main
}
