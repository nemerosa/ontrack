package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHub
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningNotification
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test

/**
 * Testing the notifications registered in the AV configuration.
 */
@TestOnGitHub
class ACCAutoVersioningConfigNotifications : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning notification for all scopes`() {
        val projectGroup = uid("tar-g-")
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                project {

                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "another-version", // This will raise an error
                                    notifications = listOf(
                                        AutoVersioningNotification(
                                            channel = "in-memory",
                                            config = mapOf("group" to projectGroup).asJson(),
                                        )
                                    )
                                )
                            )
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        // Check an error notification has been received at target branch level
                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Error notification at target level",
                            onTimeout = displayNotifications(projectGroup)
                        ) {
                            ontrack.notifications.inMemory.group(projectGroup).firstOrNull() ==
                                    """
                                        Auto versioning of ${project.name}/$name for dependency ${dependency.project.name} version "2.0.0" has failed.
    
                                        Cannot find version in "gradle.properties".
    
                                        Error: Cannot find version in "gradle.properties".
                                    """.trimIndent()
                        }

                    }
                }
            }
        }
    }

}