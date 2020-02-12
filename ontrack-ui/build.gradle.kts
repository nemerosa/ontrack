import net.nemerosa.versioning.VersioningExtension
import org.apache.commons.lang3.time.DateFormatUtils
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    groovy
    `java-library`
}

apply(plugin = "org.springframework.boot")

dependencies {
    implementation(project(":ontrack-ui-support"))
    implementation(project(":ontrack-ui-graphql"))
    implementation(project(":ontrack-extension-api"))
    implementation(project(":ontrack-extension-support"))
    implementation(project(":ontrack-job"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-text")
    implementation("commons-io:commons-io")

    runtimeOnly(project(":ontrack-service"))
    runtimeOnly(project(":ontrack-repository-impl"))
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-core")

    // Metric runtimes
    runtimeOnly("io.micrometer:micrometer-registry-influx")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testImplementation(project(":ontrack-test-utils"))
    testImplementation(project(":ontrack-it-utils"))
    testImplementation(project(":ontrack-extension-general"))
    testImplementation(project(path = ":ontrack-extension-api", configuration = "tests"))
    testImplementation(project(path = ":ontrack-model", configuration = "tests"))
    testImplementation(project(path = ":ontrack-ui-graphql", configuration = "tests"))
    testImplementation("org.codehaus.groovy:groovy")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // List of extensions to include in core
    runtimeOnly(project(":ontrack-extension-general"))
    runtimeOnly(project(":ontrack-extension-jenkins"))
    runtimeOnly(project(":ontrack-extension-jira"))
    runtimeOnly(project(":ontrack-extension-svn"))
    runtimeOnly(project(":ontrack-extension-artifactory"))
    runtimeOnly(project(":ontrack-extension-git"))
    runtimeOnly(project(":ontrack-extension-github"))
    runtimeOnly(project(":ontrack-extension-gitlab"))
    runtimeOnly(project(":ontrack-extension-stash"))
    runtimeOnly(project(":ontrack-extension-combined"))
    runtimeOnly(project(":ontrack-extension-ldap"))
    runtimeOnly(project(":ontrack-extension-stale"))
    runtimeOnly(project(":ontrack-extension-vault"))
    runtimeOnly(project(":ontrack-extension-influxdb"))
    runtimeOnly(project(":ontrack-extension-sonarqube"))
    runtimeOnly(project(":ontrack-extension-elasticsearch"))
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
 * Generates the version information in a file, useable from the code
 */

val generateVersionInfo by tasks.registering {
    doLast {
        // Version information
        val info = rootProject.extensions.getByName<VersioningExtension>("versioning").info
        // Current date
        val timestamp = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(System.currentTimeMillis())
        // Output file
        val file = project.file("src/main/resources/application.properties")
        file.writeText("""
            # This file is generated at build time to contain version information
            # Do not edit it, do not commit it
            ontrack.ersion.date = $timestamp
            ontrack.version.display = ${info.display}
            ontrack.version.full = ${info.full}
            ontrack.version.branch = ${info.branchId}
            ontrack.version.build = ${info.build}
            ontrack.version.commit = ${info.commit}
            ontrack.version.source = ${info.branch}
            ontrack.version.sourceType = ${info.branchType}
            # For the /manage/info endpoint
            info.app.version = ${info.display}
            info.build.date = $timestamp
            info.build.display = ${info.display}
            info.build.full = ${info.full}
            info.build.branch = ${info.branchId}
            info.build.build = ${info.build}
            info.build.commit = ${info.commit}
            info.build.source = ${info.branch}
            info.build.sourceType = ${info.branchType}
        """.trimIndent())
    }
}

/**
 * Dependencies
 */

tasks.named<Jar>("jar") {
    dependsOn(copyWebResources)
    dependsOn(generateVersionInfo)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(copyWebResources)
    dependsOn(generateVersionInfo)
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

val bootJar = tasks.getByName<BootJar>("bootJar") {
    // Allowing the declaration of external extensions, packaged using the Spring Boot Module format
    manifest {
        attributes("Main-Class" to "org.springframework.boot.loader.PropertiesLauncher")
    }
}

rootProject.tasks.named<Zip>("publicationPackage") {
    from(bootJar)
}
