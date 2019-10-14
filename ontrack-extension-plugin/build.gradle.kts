import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    groovy
}

description = "Gradle plugin to create an Ontrack extension."

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

val springBootVersion: String by project
val kotlinVersion: String by project

dependencies {
    implementation(gradleApi())
    implementation("gradle.plugin.com.liferay:gradle-plugins-node:4.3.3")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
}

tasks.named<ProcessResources>("processResources") {
    filter(
            ReplaceTokens::class,
            "tokens" to mapOf(
                    "version" to version as String,
                    "kotlinVersion" to kotlinVersion
            )
    )
}
