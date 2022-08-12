import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.android)
    implementation(libs.plugin.kotlin)
}

tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile::class.java).configureEach {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.name
}

gradlePlugin {
    plugins {
        create("appyx-publish-android") {
            id = "appyx-publish-android"
            implementationClass = "AndroidAppyxPublishPlugin"
        }
        create("appyx-publish-java") {
            id = "appyx-publish-java"
            implementationClass = "JavaAppyxPublishPlugin"
        }
    }
}
