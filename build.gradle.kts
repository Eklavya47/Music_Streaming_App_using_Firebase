// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        // Make sure that you have the following two repositories
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }

    dependencies {

        // Add the Maven coordinates and latest version of the plugin
        classpath ("com.google.gms:google-services:4.4.0")
    }
}
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}