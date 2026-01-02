plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
}
