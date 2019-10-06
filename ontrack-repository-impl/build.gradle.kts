dependencies {
    compile(project(":ontrack-database"))
    compile(project(":ontrack-repository"))
    compile(project(":ontrack-repository-support"))
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile("org.springframework:spring-context")
    compile("org.slf4j:slf4j-api")
    compile("org.flywaydb:flyway-core")

    testCompile(project(":ontrack-it-utils"))
    testRuntime(project(":ontrack-service"))
}
