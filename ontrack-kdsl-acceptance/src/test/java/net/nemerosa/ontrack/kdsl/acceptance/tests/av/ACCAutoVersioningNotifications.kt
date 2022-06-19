package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlayground
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test

@TestOnGitHubPlayground
class ACCAutoVersioningNotifications : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning notification in case of error`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")

                // Subscribing at source level
                val dependencyGroup = uid("dep-g-")
                ontrack.notifications.subscribe(
                    channel = "in-memory",
                    channelConfig = mapOf("group" to dependencyGroup),
                    keywords = null,
                    events = listOf("auto-versioning-error", "auto-versioning-success"),
                    projectEntity = dependency.project,
                )

                project {

                    // Subscribing at target level
                    val projectGroup = uid("tar-g-")
                    ontrack.notifications.subscribe(
                        channel = "in-memory",
                        channelConfig = mapOf("group" to projectGroup),
                        keywords = null,
                        events = listOf("auto-versioning-error", "auto-versioning-success"),
                        projectEntity = this,
                    )

                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "another-version",
                                )
                            )
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        // Check an error notification has been received at source level
                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Error notification at dependency level",
                            onTimeout = displayNotifications(dependencyGroup)
                        ) {
                            ontrack.notifications.inMemory.group(dependencyGroup).firstOrNull() ==
                                    """
                                        Auto versioning of ${project.name}/$name for dependency ${dependency.project.name} version "2.0.0" has failed.
    
                                        Cannot find version in "gradle.properties".
    
                                        Error: Cannot find version in "gradle.properties".
                                    """.trimIndent()
                        }

                        // Check an error notification has been received at source level
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

    @Test
    fun `Auto versioning notification for a client merge`() {
        withTestGitHubRepository {
            withAutoVersioning {
                val dependency = branchWithPromotion(promotion = "IRON")

                // Subscribing at source level
                val dependencyGroup = uid("dep-g-")
                ontrack.notifications.subscribe(
                    channel = "in-memory",
                    channelConfig = mapOf("group" to dependencyGroup),
                    keywords = null,
                    events = listOf("auto-versioning-error", "auto-versioning-success"),
                    projectEntity = dependency.project,
                )

                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }

                project {

                    // Subscribing at target level
                    val projectGroup = uid("tar-g-")
                    ontrack.notifications.subscribe(
                        channel = "in-memory",
                        channelConfig = mapOf("group" to projectGroup),
                        keywords = null,
                        events = listOf("auto-versioning-error", "auto-versioning-success"),
                        projectEntity = this,
                    )

                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                )
                            )
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        // Check a success notification has been received at source level
                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Success notification at dependency level",
                            onTimeout = displayNotifications(dependencyGroup)
                        ) {
                            ontrack.notifications.inMemory.group(dependencyGroup).firstOrNull() ==
                                    """
                                        Auto versioning of ${project.name}/$name for dependency ${dependency.project.name} version "2.0.0" has been done.
                                        
                                        Auto versioning PR has been created, approved and merged.
                                        
                                        Pull request #1
                                    """.trimIndent()
                        }

                        // Check a success notification has been received at target level
                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Success notification at target level",
                            onTimeout = displayNotifications(projectGroup)
                        ) {
                            ontrack.notifications.inMemory.group(projectGroup).firstOrNull() ==
                                    """
                                        Auto versioning of ${project.name}/$name for dependency ${dependency.project.name} version "2.0.0" has been done.
                                        
                                        Auto versioning PR has been created, approved and merged.
                                        
                                        Pull request #1
                                    """.trimIndent()
                        }

                    }
                }
            }
        }
    }

    private fun displayNotifications(group: String): () -> Unit = {
        println("Notifications for group $group:")
        ontrack.notifications.inMemory.group(group).forEach {
            println("--------------")
            println(it)
        }
    }

}