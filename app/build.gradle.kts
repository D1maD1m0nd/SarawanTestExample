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
        getByName("debug") {
            val keystorePropertiesFile = file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            storeFile = file(keystoreProperties["FILE_PATH"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
            keyAlias = keystoreProperties["ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
        }

        create("release") {
            val keystorePropertiesFile = file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            storeFile = file(keystoreProperties["FILE_PATH"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
            keyAlias = keystoreProperties["ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
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

        val properties = Properties()
        val propertiesFile = file("config_constants.properties")
        properties.load(FileInputStream(propertiesFile))
        val apiKeyMap = properties["api_key_map_kit"] as String
        val apiKeyGeocoder = properties["api_key_geocoder"] as String
        buildConfigField("String", "MAP_API_KEY", apiKeyMap)
        buildConfigField("String", "GEOCODER_API_KEY", apiKeyGeocoder)
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
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

    //security
    implementation(AppDependencies.security)

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
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

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