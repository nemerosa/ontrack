package net.nemerosa.ontrack.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Launching acceptance tests
 */
open class RemoteAcceptanceTest : DefaultTask() {

    @Input
    var disableSsl = false

    @Input
    var acceptanceContext = "all"

    @Input
    var acceptanceJar = project.properties["acceptanceJar"] as String

    @Input
    var acceptanceUrl: String = "http://localhost:8080"

    @Input
    var acceptanceUrlFn: (() -> String)? = null

    @Input
    var acceptancePassword = "admin"

    @Input
    var acceptanceTimeout = 120

    @Input
    var acceptanceImplicitWait = 5

    @TaskAction
    fun launch() {
        // URL
        val url = when {
            acceptanceUrlFn != null -> acceptanceUrlFn!!()
            else -> acceptanceUrl
        }
        // Logging
        logger.info("Acceptance library at $acceptanceJar")
        logger.info("Application at $url")
        // Running the tests
        project.exec {
            workingDir(project.projectDir)
            executable("java")
            args = listOf(
                    "-jar",
                    acceptanceJar,
                    "--ontrack.acceptance.url=$url",
                    "--ontrack.acceptance.admin=$acceptancePassword",
                    "--ontrack.acceptance.disable-ssl=$disableSsl",
                    "--ontrack.acceptance.context=$acceptanceContext",
                    "--ontrack.acceptance.timeout=$acceptanceTimeout",
                    "--ontrack.acceptance.implicit-wait=$acceptanceImplicitWait"
            )
        }
    }
}