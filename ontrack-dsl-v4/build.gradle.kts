plugins {
    groovy
    `java-library`
}

description = "DSL for Ontrack."

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

/**
 * Dependencies of the DSL module must be carefully controlled
 * outside of the core modules
 */

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.8.9")
    api("org.codehaus.groovy:groovy:2.5.8")
    api("org.codehaus.groovy:groovy-json:2.5.8")

    implementation("org.codehaus.groovy:groovy-templates:2.5.8")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.apache.httpcomponents:httpclient:4.5.3")
    implementation("org.apache.httpcomponents:httpcore:4.4.6")
    implementation("org.apache.httpcomponents:httpmime:4.5.3")
    implementation("commons-logging:commons-logging:1.2")
    implementation("net.jodah:failsafe:0.9.2")

    testImplementation("junit:junit:4.12")
}

if (project.hasProperty("documentation")) {
    tasks.named<Jar>("javadocJar") {
        from("javadoc")
        from("groovydoc")
    }
}
