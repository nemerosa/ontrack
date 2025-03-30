plugins {
    `java-library`
}

dependencies {
    api(project(":ontrack-repository"))
    api("org.springframework:spring-jdbc")

    implementation(project(":ontrack-database"))
    implementation(project(":ontrack-repository-support"))
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-actuator")
    implementation("org.slf4j:slf4j-api")
    implementation("org.flywaydb:flyway-core")
    implementation("org.apache.commons:commons-lang3")

    testImplementation(project(":ontrack-it-utils"))

    testRuntimeOnly(project(":ontrack-service"))
}


val integrationTest by tasks.registering(Test::class) {
    group = "verification"
    description = "Integration tests"
    useJUnitPlatform()

    // Only include classes whose names end with 'IT'
    include("**/*IT.class")
    // Set the test classes directory to be the same as the unit tests
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    shouldRunAfter("test")
    minHeapSize = "128m"
    maxHeapSize = "3072m"
//    dependsOn(":integrationTestComposeUp")
//    finalizedBy(":integrationTestComposeDown")
}

// Synchronization with shutting down the database
rootProject.tasks.named("integrationTestComposeDown") {
    mustRunAfter(integrationTest)
}

// Inclusion in lifecycle
tasks.check {
    dependsOn(integrationTest)
}