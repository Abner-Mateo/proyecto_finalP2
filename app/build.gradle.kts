plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.prox0"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.prox0"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Para HTTP requests (OpenAI API)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Para SQLite
    implementation("androidx.sqlite:sqlite:2.4.0")

    // Para RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Para CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // Para ViewBinding
    implementation("androidx.databinding:databinding-runtime:8.2.0")

    // Para notificaciones inteligentes
    implementation("androidx.work:work-runtime:2.9.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}