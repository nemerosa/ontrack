plugins {
    groovy
}

dependencies {
    compile(project(":ontrack-common"))
    compile(project(":ontrack-json"))
    compile(project(":ontrack-job"))
    compile("com.google.guava:guava")
    compile("org.apache.commons:commons-text")
    compile("org.springframework:spring-context")
    compile("org.springframework.security:spring-security-core")
    compile("javax.validation:validation-api")
    compile("org.slf4j:slf4j-api")
    compile("org.springframework.boot:spring-boot-starter-actuator")

    testCompile("org.codehaus.groovy:groovy")
    testCompile(project(":ontrack-test-utils"))
}

val testJar by tasks.registering(Jar::class) {
    archiveClassifier.set("tests")
    from(sourceSets["test"].output)
}

configure<PublishingExtension> {
    publications {
        maybeCreate<MavenPublication>("mavenCustom").artifact(tasks["testJar"])
    }
}

tasks["assemble"].dependsOn("testJar")

val tests by configurations.creating

artifacts {
    add("tests", testJar)
}
