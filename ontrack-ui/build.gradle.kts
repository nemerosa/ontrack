import net.nemerosa.versioning.VersioningExtension
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    `java-library`
}

apply(plugin = "org.springframework.boot")

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")
    api(project(":ontrack-ui-support"))
    api(project(":ontrack-ui-graphql"))
    api(project(":ontrack-extension-api"))
    api(project(":ontrack-extension-support"))

    implementation(project(":ontrack-job"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-text")
    implementation("commons-io:commons-io")
    implementation("jakarta.validation:jakarta.validation-api")

    runtimeOnly(project(":ontrack-service"))
    runtimeOnly(project(":ontrack-repository-impl"))
    runtimeOnly(project(":ontrack-rabbitmq"))
    runtimeOnly(project(":ontrack-database"))

    // Metric runtimes
    // TODO runtimeOnly("io.micrometer:micrometer-registry-influx")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    // TODO runtimeOnly("io.micrometer:micrometer-registry-elastic")

    // TODO Logging extensions
    runtimeOnly("net.logstash.logback:logstash-logback-encoder")

    testImplementation(testFixtures(project(":ontrack-ui-support")))
//    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
//    testImplementation(project(":ontrack-extension-general"))
//    testImplementation(project(":ontrack-extension-casc"))
//    testImplementation(project(path = ":ontrack-extension-casc", configuration = "tests"))
    testImplementation(testFixtures(project(":ontrack-model")))
    testImplementation(testFixtures(project(":ontrack-ui-graphql")))
    testImplementation(testFixtures(project(":ontrack-extension-api")))
//    testImplementation("com.networknt:json-schema-validator")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // List of extensions needed for the documentation generation
//    testImplementation(project(":ontrack-extension-notifications"))
//    testImplementation(project(":ontrack-extension-workflows"))
    testImplementation("org.junit.platform:junit-platform-suite-api")
    testImplementation("org.junit.platform:junit-platform-suite-engine")

    // List of extensions to include in core
    // TODO runtimeOnly(project(":ontrack-extension-general"))
    // TODO runtimeOnly(project(":ontrack-extension-ldap"))
    // TODO runtimeOnly(project(":ontrack-extension-oidc"))
    // TODO runtimeOnly(project(":ontrack-extension-jenkins"))
    // TODO runtimeOnly(project(":ontrack-extension-jira"))
    // TODO runtimeOnly(project(":ontrack-extension-artifactory"))
    // TODO runtimeOnly(project(":ontrack-extension-git"))
    // TODO runtimeOnly(project(":ontrack-extension-github"))
    // TODO runtimeOnly(project(":ontrack-extension-gitlab"))
    // TODO runtimeOnly(project(":ontrack-extension-stash"))
    // TODO runtimeOnly(project(":ontrack-extension-bitbucket-cloud"))
    // TODO runtimeOnly(project(":ontrack-extension-combined"))
    // TODO runtimeOnly(project(":ontrack-extension-stale"))
    // TODO runtimeOnly(project(":ontrack-extension-vault"))
    // TODO runtimeOnly(project(":ontrack-extension-influxdb"))
    // TODO runtimeOnly(project(":ontrack-extension-sonarqube"))
    // TODO runtimeOnly(project(":ontrack-extension-indicators"))
    // TODO runtimeOnly(project(":ontrack-extension-casc"))
    // TODO runtimeOnly(project(":ontrack-extension-elastic"))
    // TODO runtimeOnly(project(":ontrack-extension-slack"))
    // TODO runtimeOnly(project(":ontrack-extension-chart"))
    // TODO runtimeOnly(project(":ontrack-extension-delivery-metrics"))
    // TODO runtimeOnly(project(":ontrack-extension-auto-versioning"))
    // TODO runtimeOnly(project(":ontrack-extension-license"))
    // TODO runtimeOnly(project(":ontrack-extension-tfc"))
    // TODO runtimeOnly(project(":ontrack-extension-recordings"))
    // TODO runtimeOnly(project(":ontrack-extension-notifications"))
    // TODO runtimeOnly(project(":ontrack-extension-hook"))
    // TODO runtimeOnly(project(":ontrack-extension-queue"))
    // TODO runtimeOnly(project(":ontrack-extension-workflows"))
    // TODO runtimeOnly(project(":ontrack-extension-environments"))
}

/**
 * Cleaning the Web resources
 */

//tasks.named<Delete>("clean") {
//    delete("src/main/resources/application.properties")
//    delete("src/main/resources/static")
//}

/**
 * Copy of Web resources before packaging
 */

//val copyWebResources by tasks.registering {
//    dependsOn(":ontrack-web:prod")
//    doLast {
//        project.copy {
//            from(project(":ontrack-web").file("build/web/prod"))
//            into("src/main/resources/static")
//        }
//    }
//}

/**
 * Dependencies
 */

tasks.named<Jar>("jar") {
    enabled = true
    // dependsOn(copyWebResources)
    dependsOn("bootBuildInfo")
}

tasks.named<ProcessResources>("processResources") {
    // dependsOn(copyWebResources)
    dependsOn("bootBuildInfo")
}

tasks.named<BootRun>("bootRun") {
    dependsOn("bootJar")
    // dependsOn(":ontrack-web:dev")

    // Running with `dev` profile by default with `bootRun`
    args("--spring.profiles.active=dev")
}

/**
 * Spring boot packaging
 */

configure<SpringBootExtension> {
    val info = rootProject.extensions.getByName<VersioningExtension>("versioning").info
    buildInfo {
        properties {
            time = null
            additional = mapOf(
                "full" to info.full,
                "branch" to info.branch,
                "build" to info.build,
                "commit" to info.commit,
                "dirty" to info.dirty,
            )
        }
    }
}

val bootJar = tasks.getByName<BootJar>("bootJar") {
    // Specific classifier
    archiveClassifier.set("app")
    // Allowing the declaration of external extensions, packaged using the Spring Boot Module format
    manifest {
        attributes("Main-Class" to "org.springframework.boot.loader.PropertiesLauncher")
    }
}
