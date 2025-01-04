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
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-text")
    implementation("commons-io:commons-io")
    implementation("jakarta.validation:jakarta.validation-api")

    runtimeOnly(project(":ontrack-service"))
    runtimeOnly(project(":ontrack-repository-impl"))
    runtimeOnly(project(":ontrack-rabbitmq"))
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-core")

    // Metric runtimes
    runtimeOnly("io.micrometer:micrometer-registry-influx")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.micrometer:micrometer-registry-elastic")

    // Logging extensions
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.0.1")

    testImplementation(project(path = ":ontrack-ui-support", configuration = "tests"))
    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-support", configuration = "tests"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // List of extensions needed for the documentation generation
    testImplementation(project(":ontrack-extension-notifications"))
    testImplementation(project(":ontrack-extension-workflows"))
    testImplementation("org.junit.platform:junit-platform-suite-api")
    testImplementation("org.junit.platform:junit-platform-suite-engine")

    // List of extensions to include in core
    runtimeOnly(project(":ontrack-extension-general"))
    runtimeOnly(project(":ontrack-extension-ldap"))
    runtimeOnly(project(":ontrack-extension-oidc"))
    runtimeOnly(project(":ontrack-extension-jenkins"))
    runtimeOnly(project(":ontrack-extension-jira"))
    runtimeOnly(project(":ontrack-extension-artifactory"))
    runtimeOnly(project(":ontrack-extension-git"))
    runtimeOnly(project(":ontrack-extension-github"))
    runtimeOnly(project(":ontrack-extension-gitlab"))
    runtimeOnly(project(":ontrack-extension-stash"))
    runtimeOnly(project(":ontrack-extension-bitbucket-cloud"))
    runtimeOnly(project(":ontrack-extension-combined"))
    runtimeOnly(project(":ontrack-extension-stale"))
    runtimeOnly(project(":ontrack-extension-vault"))
    runtimeOnly(project(":ontrack-extension-influxdb"))
    runtimeOnly(project(":ontrack-extension-sonarqube"))
    runtimeOnly(project(":ontrack-extension-indicators"))
    runtimeOnly(project(":ontrack-extension-casc"))
    runtimeOnly(project(":ontrack-extension-elastic"))
    runtimeOnly(project(":ontrack-extension-slack"))
    runtimeOnly(project(":ontrack-extension-chart"))
    runtimeOnly(project(":ontrack-extension-delivery-metrics"))
    runtimeOnly(project(":ontrack-extension-auto-versioning"))
    runtimeOnly(project(":ontrack-extension-license"))
    runtimeOnly(project(":ontrack-extension-tfc"))
    runtimeOnly(project(":ontrack-extension-recordings"))
    runtimeOnly(project(":ontrack-extension-notifications"))
    runtimeOnly(project(":ontrack-extension-hook"))
    runtimeOnly(project(":ontrack-extension-queue"))
    runtimeOnly(project(":ontrack-extension-workflows"))
    runtimeOnly(project(":ontrack-extension-environments"))
}

/**
 * Cleaning the Web resources
 */

tasks.named<Delete>("clean") {
    delete("src/main/resources/application.properties")
    delete("src/main/resources/static")
}

/**
 * Copy of Web resources before packaging
 */

val copyWebResources by tasks.registering {
    dependsOn(":ontrack-web:prod")
    doLast {
        project.copy {
            from(project(":ontrack-web").file("build/web/prod"))
            into("src/main/resources/static")
        }
    }
}

/**
 * Dependencies
 */

tasks.named<Jar>("jar") {
    enabled = true
    dependsOn(copyWebResources)
    dependsOn("bootBuildInfo")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(copyWebResources)
    dependsOn("bootBuildInfo")
}

tasks.named<BootRun>("bootRun") {
    dependsOn("bootJar")
    dependsOn(":ontrack-web:dev")

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

/**
 * Publication of artifacts
 */

publishing {
    publications {
        named<MavenPublication>("mavenCustom") {
            artifact(bootJar) {
                classifier = "app"
            }
            artifact("build/graphql.json") {
                classifier = "graphql.json"
            }
        }
    }
}

/**
 * Publication of artifacts, mostly the one of the `graphql.json` file, depends
 * on the integration tests having run.
 */

tasks.withType(AbstractPublishToMaven::class.java) {
    dependsOn("integrationTest")
}
