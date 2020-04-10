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
    implementation("com.github.node-gradle:gradle-node-plugin:2.2.3")
    implementation("net.nemerosa.ontrack:ontrack-dsl:3.40.0") {
        exclude(module = "groovy-all")
    }
}
