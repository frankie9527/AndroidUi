plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.amap.location.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.amap.location.demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file("../location.jks")
            storePassword = "location"
            keyAlias = "location"
            keyPassword = "location"
        }

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
//    implementation("com.amap.api:3dmap:10.0.600")

    implementation("com.amap.api:navi-3dmap:10.0.700_3dmap10.0.700"){
        exclude(group = "com.amap.api", module = "search")
    }
    implementation("com.amap.api:search:9.7.1")
}