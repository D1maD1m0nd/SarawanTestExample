import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

kapt {
    arguments {
        arg("dagger.fastInit", "enabled")
        arg("dagger.formatGeneratedSource", "enabled")
    }
}

android {
    signingConfigs {
        val keystorePropertiesFile = file("keystore.properties")
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        register("Sarawan_release") {
            keyAlias = keystoreProperties["ALIAS"] as String
            keyPassword = keystoreProperties["STORE_PASSWORD"] as String
            storeFile = file(keystoreProperties["FILE_PATH"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
        }
    }
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "ru.sarawan.android"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    viewBinding {
        android.buildFeatures.viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //std lib
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    //UI
    implementation(AppDependencies.ui)

    //Dagger
    implementation(AppDependencies.diImpl)

    //retrofit
    implementation(AppDependencies.retrofitImpl)

    //Recycler View adapter
    implementation(AppDependencies.recyclerViewAdapterImpl)


    //room
    implementation(AppDependencies.roomImpl)

    //kapt
    kapt(AppDependencies.kapt)

    //RxJava
    implementation(AppDependencies.rxJavaImpl)

    //Navigation
    implementation(AppDependencies.navImpl)

    //FireBase
    implementation(AppDependencies.fireBaseImpl)

    //ViewModel
    implementation(AppDependencies.viewModelImpl)

    //test libs
    testImplementation(AppDependencies.testLibraries)
    androidTestImplementation(AppDependencies.androidTestLibraries)
}