import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

// Node environment

configure<NodeExtension> {
    version.set("20.2.0")
    npmVersion.set("9.6.6")
    download.set(true)
}

// Test environment

val isCI = System.getenv("CI") == "true"

val playwrightInstall by tasks.registering(NpmTask::class) {
    dependsOn("npmInstall")
    args.set(listOf("run", "playwright-install"))
}

val playwrightSetup by tasks.registering(NpmTask::class) {
    dependsOn(playwrightInstall)
    args.set(listOf("run", "playwright-setup"))
}

// Testing

val uiTest by tasks.registering(NpmTask::class) {
    dependsOn(playwrightSetup)
    if (!isCI) {
        dependsOn(":ontrack-kdsl-acceptance:kdslAcceptanceTestComposeUp")
        finalizedBy(":ontrack-kdsl-acceptance:kdslAcceptanceTestComposeDown")
    }

    args.set(listOf("run", "test"))
    environment.put("JUNIT_REPORT_PATH", "reports/main/junit/report.xml")
    environment.put("HTML_REPORT_PATH", "reports/main/html")
}

// Specialized tests

val uiLdapTest by tasks.registering(NpmTask::class) {
    dependsOn(playwrightSetup)
    dependsOn(":ontrack-kdsl-acceptance:kdslLdapComposeUp")
    finalizedBy(":ontrack-kdsl-acceptance:kdslLdapComposeDown")

    shouldRunAfter(uiTest)
    shouldRunAfter(":ontrack-kdsl-acceptance:kdslAcceptanceTestComposeDown")

    args.set(listOf("run", "test-ldap"))
    environment.put("JUNIT_REPORT_PATH", "reports/ldap/junit/report.xml")
    environment.put("HTML_REPORT_PATH", "reports/ldap/html")
}

// All tests

val uiTests by tasks.registering {
    dependsOn(uiTest)
    dependsOn(uiLdapTest)
}
