plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    compile(gradleApi())
    compile(gradleKotlinDsl())
    compile("gradle.plugin.com.liferay:gradle-plugins-node:5.1.1")
    compile("net.nemerosa.ontrack:ontrack-dsl:3.40.0") {
        exclude(module = "groovy-all")
    }
}
