package net.nemerosa.ontrack.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Launching acceptance tests
 */
class RemoteAcceptanceTest extends DefaultTask {

    boolean disableSsl = false

    String acceptanceContext = 'all'

    String acceptanceJar = project.properties.acceptanceJar

    def acceptanceUrl

    String acceptancePassword = 'admin'

    int acceptanceTimeout = 120

    int acceptanceImplicitWait = 5

    @TaskAction
    def launch() {
        // URL
        String url
        if (!acceptanceUrl) throw new GradleException("acceptanceUrl property is not defined for ${name}")
        else if (acceptanceUrl instanceof Closure) {
            url = acceptanceUrl()
        } else {
            url = acceptanceUrl as String
        }
        // Logging
        println "[${name}] Acceptance library at ${acceptanceJar}"
        println "[${name}] Application at ${url}"
        // Running the tests
        project.exec {
            workingDir project.projectDir
            executable 'java'
            args '-jar', "${acceptanceJar}",
                    "--ontrack.acceptance.url=${url}",
                    "--ontrack.acceptance.admin=${acceptancePassword}",
                    "--ontrack.acceptance.disable-ssl=${disableSsl}",
                    "--ontrack.acceptance.context=${acceptanceContext}",
                    "--ontrack.acceptance.timeout=${acceptanceTimeout}",
                    "--ontrack.acceptance.implicit-wait=${acceptanceImplicitWait}"
        }
    }
}