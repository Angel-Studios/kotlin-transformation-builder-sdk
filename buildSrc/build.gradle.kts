plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    //val libs = catalogs.named("libs")
    //implementation(libs.findLibrary("gradle-kotlin").get())
    implementation(libs.gradle.kotlin)
}
