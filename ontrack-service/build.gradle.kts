plugins {
    groovy
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile("org.springframework.boot:spring-boot-starter-cache")
    compile(project(":ontrack-model"))
    compile(project(":ontrack-repository"))
    compile(project(":ontrack-extension-api"))
    compile(project(":ontrack-job"))
    compile("org.springframework.security:spring-security-core")
    compile("org.springframework.security:spring-security-config")
    compile("org.springframework.security:spring-security-ldap")
    compile("org.slf4j:slf4j-api")
    compile("commons-io:commons-io")
    compile("org.codehaus.groovy:groovy")
    compile("org.kohsuke:groovy-sandbox")
    compile("org.jgrapht:jgrapht-core")
    compile("com.github.ben-manes.caffeine:caffeine")
    compile("org.flywaydb:flyway-core")

    testCompile(project(":ontrack-it-utils"))
    testCompile(project(path = ":ontrack-extension-api", configuration = "tests"))
    testRuntime(project(":ontrack-repository-impl"))
    testRuntime("io.micrometer:micrometer-registry-prometheus")

}
