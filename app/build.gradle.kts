plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.eskimo.findmyphone.locatemydevice.trackmymobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.eskimo.findmyphone.locatemydevice.trackmymobile"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        register("release") {
            storeFile = file("../keys/keystore-eskimo")
            storePassword = "eskimo@123"
            keyAlias = "key0"
            keyPassword = "eskimo@123"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
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
    viewBinding {
        enable = true
    }
    flavorDimensions += "default"
    productFlavors {
        create("dev") {
            //use id test when dev
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField(
                "String",
                "ad_banner_splash",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "ad_interstitial_splash",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField(
                "String",
                "ad_interstitial_create",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField("String", "ad_banner", "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField(
                "String",
                "ads_open_resume",
                "\"ca-app-pub-3940256099942544/9257395921\""
            )
            buildConfigField(
                "String",
                "ad_native_language",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "ad_native_language_2",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "ad_native_home",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "ad_native_onboarding",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "ad_native_exit_dialog",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "ad_native_onboarding_high",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField("String", "ads_open_app", "\"ca-app-pub-3940256099942544/9257395921\"")
            buildConfigField("Boolean", "env_dev", "true")
        }
        create("prod") {
            //use id test when dev
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-1848319716441980~9840657303"
            buildConfigField(
                "String",
                "ad_banner_splash",
                "\"ca-app-pub-1848319716441980/9724368546\""
            )
            buildConfigField(
                "String",
                "ad_interstitial_splash",
                "\"ca-app-pub-1848319716441980/3964877032\""
            )
            buildConfigField(//Chưa sửa gì
                "String",
                "ad_interstitial_create",
                "\"ca-app-pub-1848319716441980/3964877032\""
            )
            buildConfigField("String", "ad_banner", "\"ca-app-pub-1848319716441980/1071120380\"")
            buildConfigField(
                "String",
                "ads_open_resume",
                "\"ca-app-pub-1848319716441980/1247499767\""
            )
            buildConfigField(
                "String",
                "ad_native_language",
                "\"ca-app-pub-1848319716441980/8949610409\""
            )
            buildConfigField(
                "String",
                "ad_native_language_2",
                "\"ca-app-pub-1848319716441980/6399468681\""
            )
            buildConfigField(
                "String",
                "ad_native_home",
                "\"ca-app-pub-1848319716441980/2384202059\""
            )
            buildConfigField(
                "String",
                "ad_native_onboarding",
                "\"ca-app-pub-1848319716441980/7321566480\""
            )
            buildConfigField(
                "String",
                "ad_native_onboarding_high",
                "\"ca-app-pub-1848319716441980/7321566480\""
            )
            buildConfigField(
                "String",
                "ad_native_exit_dialog",
                "\"ca-app-pub-1848319716441980/3378839088\""
            )
            buildConfigField("String", "ads_open_app", "\"ca-app-pub-1848319716441980/5521786698\"")
            buildConfigField("Boolean", "env_dev", "false")
        }
    }
}

dependencies {
    implementation(files("libs/musicg-1.4.2.0.jar"))
    implementation(files("libs/TarsosDSP-Android-latest-bin.jar"))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.chromium.net:cronet-embedded:119.6045.31")
    implementation("androidx.activity:activity:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.jakewharton.rxbinding2:rxbinding-kotlin:2.2.0")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation(project(":admob"))
    implementation("com.google.android.gms:play-services-ads:23.1.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.7.1")

}