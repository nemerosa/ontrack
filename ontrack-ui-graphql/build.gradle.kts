plugins {
    groovy
}

dependencies {

    compile(project(":ontrack-ui-support"))
    compile("com.graphql-java:graphql-java")
    compile("org.springframework:spring-tx")

    testCompile(project(":ontrack-test-utils"))
    testCompile(project(":ontrack-it-utils"))
    testCompile(project(path = ":ontrack-extension-api", configuration = "tests"))
    testCompile(project(path = ":ontrack-model", configuration = "tests"))
    testCompile("org.codehaus.groovy:groovy")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile(project(":ontrack-repository-impl"))
    testCompile(project(":ontrack-extension-general"))

    testRuntime(project(":ontrack-service"))

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
