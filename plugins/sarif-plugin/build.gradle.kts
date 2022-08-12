import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.plugin)
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.21.0")
}

tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile::class.java).configureEach {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.name
}

gradlePlugin {
    plugins {
        create("appyx-collect-sarif") {
            id = "appyx-collect-sarif"
            implementationClass = "CollectSarifPlugin"
        }
        create("appyx-report-lint-sarif") {
            id = "appyx-report-lint-sarif"
            implementationClass = "ReportLintSarifPlugin"
        }
        create("appyx-report-detekt-sarif") {
            id = "appyx-report-detekt-sarif"
            implementationClass = "ReportDetektSarifPlugin"
        }
    }
}
