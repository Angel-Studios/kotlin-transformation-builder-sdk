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
        useEsModules()
        browser()
        nodejs()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
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

publishing {
    repositories {
        maven {
            name = "S3"
            url = uri("s3://angelstudios-apps-artifacts.s3.amazonaws.com")
            credentials(AwsCredentials::class) {
                val credentials = utils.awsCredentials()
                accessKey = credentials.accessKeyId()
                secretKey = credentials.secretAccessKey()
            }
        }
    }
}
