// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath("com.google.gms:google-services:${Versions.googleService}")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navSafeArgs}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}