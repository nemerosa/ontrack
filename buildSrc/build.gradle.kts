plugins {
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
    implementation("org.apache.httpcomponents:httpclient:4.5.3")
    implementation("org.apache.httpcomponents:httpcore:4.4.6")
    implementation("net.nemerosa.ontrack:ontrack-dsl-v4:4.0-beta.20") {
        exclude(module = "groovy")
        exclude(module = "groovy-json")
    }
}
