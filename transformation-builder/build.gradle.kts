import extensions.getGitTag

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

group = "com.cloudinary"
version = getGitTag()

kotlin {
    jvm()
    js(IR) {
        browser()
        nodejs()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.uri)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.annotations.common)
            }
        }
        val jsMain by getting {
            dependencies {
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
    }
}
