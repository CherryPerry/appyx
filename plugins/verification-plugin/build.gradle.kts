import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.android)
    implementation(libs.plugin.detekt)
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
        create("appyx-lint") {
            id = "appyx-lint"
            implementationClass = "LintPlugin"
        }
        create("appyx-detekt") {
            id = "appyx-detekt"
            implementationClass = "DetektPlugin"
        }
    }
}
