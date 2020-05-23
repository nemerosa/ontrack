import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `kotlin-dsl`
}

description = "Gradle plugin to create an Ontrack extension."

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("com.github.node-gradle:gradle-node-plugin:2.2.3")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${Versions.springBootVersion}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${Versions.kotlinVersion}")
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", version)
    inputs.property("kotlinVersion", Versions.kotlinVersion)
    filter(
            ReplaceTokens::class,
            "tokens" to mapOf(
                    "version" to version as String,
                    "kotlinVersion" to Versions.kotlinVersion
            )
    )
}

gradlePlugin {
    isAutomatedPublishing = false
}
