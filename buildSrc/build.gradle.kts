plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("com.github.node-gradle:gradle-node-plugin:5.0.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.5")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
}
