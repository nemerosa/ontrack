package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.general.release
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ACCChangeLogNotifications : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Sending a change log on a promotion run`() {
        val group = uid("group-")
        withMockScmRepository(ontrack) {
            project {
                branch {
                    configuredForMockScm()
                    val pl = promotion()
                    // Subscribe to this promotion
                    pl.subscribe(
                        name = "Test",
                        channel = "in-memory",
                        channelConfig = mapOf("group" to group),
                        keywords = null,
                        events = listOf(
                            "new_promotion_run",
                        ),
                        contentTemplate = """
                            Version ${'$'}{build.release} has been released.
                            
                            ${'$'}{promotionRun.changelog}
                        """.trimIndent()
                    )

                    build {}
                    build {
                        release = "1.1.0"

                        // Mock termination commit
                        repositoryIssue("ISS-20", "Last issue before the change log")
                        withRepositoryCommit("ISS-20 Last commit before the change log")

                        promote(pl.name)  // This sends also a notification!
                    }
                    build {
                        repositoryIssue("ISS-21", "Some new feature")
                        withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                        withRepositoryCommit("ISS-21 Some fixes for a feature")
                    }
                    build {
                        repositoryIssue("ISS-22", "Some fixes are needed")
                        withRepositoryCommit("ISS-22 Fixing some bugs")
                    }
                    build {
                        release = "1.2.0"
                        repositoryIssue("ISS-23", "Some nicer UI")
                        withRepositoryCommit("ISS-23 Fixing some CSS")

                        promote(pl.name)

                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Waiting for the two notifications"
                        ) {
                            // Two promotions have been done!
                            ontrack.notifications.inMemory.group(group).size == 2
                        }

                        // Getting the last notification
                        val message = ontrack.notifications.inMemory.group(group).last()
                        assertEquals(
                            """
                                Version 1.2.0 has been released.
                                
                                * ISS-21 Some new feature
                                * ISS-22 Some fixes are needed
                                * ISS-23 Some nicer UI
                            """.trimIndent(),
                            message
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Sending a semantic change log on a promotion run`() {
        val group = uid("group-")
        withMockScmRepository(ontrack) {
            project {
                branch {
                    configuredForMockScm()
                    val pl = promotion()
                    // Subscribe to this promotion
                    pl.subscribe(
                        name = "Test",
                        channel = "in-memory",
                        channelConfig = mapOf("group" to group),
                        keywords = null,
                        events = listOf(
                            "new_promotion_run",
                        ),
                        contentTemplate = $$"""
                            Version ${build.release} has been released.
                            
                            ${promotionRun.semanticChangelog}
                        """.trimIndent()
                    )

                    build {}
                    build {
                        release = "1.1.0"

                        // Mock termination commit
                        withRepositoryCommit("chore: Last commit before the change log")

                        promote(pl.name)  // This sends also a notification!
                    }
                    build {
                        withRepositoryCommit("feat(My feature): Some commits for a feature", property = false)
                        withRepositoryCommit("feat(My feature): Some fixes for a feature")
                    }
                    build {
                        withRepositoryCommit("fix: Fixing some bugs")
                    }
                    build {
                        release = "1.2.0"
                        withRepositoryCommit("fix: Fixing some CSS")

                        promote(pl.name)

                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Waiting for the two notifications"
                        ) {
                            // Two promotions have been done!
                            ontrack.notifications.inMemory.group(group).size == 2
                        }

                        // Getting the last notification
                        val message = ontrack.notifications.inMemory.group(group).last()
                        assertEquals(
                            """
                                Version 1.2.0 has been released.
                                
                                Features:
                                
                                * My feature - Some fixes for a feature
                                * My feature - Some commits for a feature
                                
                                Fixes:
                                
                                * Fixing some CSS
                                * Fixing some bugs
                            """.trimIndent().trim(),
                            message.trim()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Sending a semantic change log on a promotion run with some issues`() {
        val group = uid("group-")
        withMockScmRepository(ontrack) {
            project {
                branch {
                    configuredForMockScm()
                    val pl = promotion()
                    // Subscribe to this promotion
                    pl.subscribe(
                        name = "Test",
                        channel = "in-memory",
                        channelConfig = mapOf("group" to group),
                        keywords = null,
                        events = listOf(
                            "new_promotion_run",
                        ),
                        contentTemplate = $$"""
                            Version ${build.release} has been released.
                            
                            ${promotionRun.semanticChangelog?issues=true}
                        """.trimIndent()
                    )

                    build {}
                    build {
                        release = "1.1.0"

                        // Mock termination commit
                        repositoryIssue("ISS-20", "Last issue before the change log")
                        withRepositoryCommit("ISS-20 Last commit before the change log")

                        promote(pl.name)  // This sends also a notification!
                    }
                    build {
                        repositoryIssue("ISS-21", "Some new feature")
                        withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                        withRepositoryCommit("ISS-21 Some fixes for a feature")
                        withRepositoryCommit("ci: Fixing the pipeline")
                        withRepositoryCommit("chore: Formatting some code")
                    }
                    build {
                        repositoryIssue("ISS-22", "Some fixes are needed")
                        withRepositoryCommit("ISS-22 Fixing some bugs")
                        withRepositoryCommit("doc: Updating the readme")
                    }
                    build {
                        release = "1.2.0"
                        repositoryIssue("ISS-23", "Some nicer UI")
                        withRepositoryCommit("ISS-23 Fixing some CSS")

                        promote(pl.name)

                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Waiting for the two notifications"
                        ) {
                            // Two promotions have been done!
                            ontrack.notifications.inMemory.group(group).size == 2
                        }

                        // Getting the last notification
                        val message = ontrack.notifications.inMemory.group(group).last()
                        assertEquals(
                            """
                                Version 1.2.0 has been released.
                                
                                Issues:
                                
                                * ISS-21 Some new feature
                                * ISS-22 Some fixes are needed
                                * ISS-23 Some nicer UI
                                
                                CI:
                                
                                * Fixing the pipeline
                                
                                Documentation:
                                
                                * Updating the readme
                                
                                Misc.:
                                
                                * Formatting some code
                            """.trimIndent().trim(),
                            message.trim()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Sending a semantic change log on a promotion run with some issues and excluding ci`() {
        val group = uid("group-")
        withMockScmRepository(ontrack) {
            project {
                branch {
                    configuredForMockScm()
                    val pl = promotion()
                    // Subscribe to this promotion
                    pl.subscribe(
                        name = "Test",
                        channel = "in-memory",
                        channelConfig = mapOf("group" to group),
                        keywords = null,
                        events = listOf(
                            "new_promotion_run",
                        ),
                        contentTemplate = $$"""
                            Version ${build.release} has been released.
                            
                            ${promotionRun.semanticChangelog?issues=true&exclude=ci}
                        """.trimIndent()
                    )

                    build {}
                    build {
                        release = "1.1.0"

                        // Mock termination commit
                        repositoryIssue("ISS-20", "Last issue before the change log")
                        withRepositoryCommit("ISS-20 Last commit before the change log")

                        promote(pl.name)  // This sends also a notification!
                    }
                    build {
                        repositoryIssue("ISS-21", "Some new feature")
                        withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                        withRepositoryCommit("ISS-21 Some fixes for a feature")
                        withRepositoryCommit("ci: Fixing the pipeline")
                        withRepositoryCommit("chore: Formatting some code")
                    }
                    build {
                        repositoryIssue("ISS-22", "Some fixes are needed")
                        withRepositoryCommit("ISS-22 Fixing some bugs")
                        withRepositoryCommit("doc: Updating the readme")
                    }
                    build {
                        release = "1.2.0"
                        repositoryIssue("ISS-23", "Some nicer UI")
                        withRepositoryCommit("ISS-23 Fixing some CSS")

                        promote(pl.name)

                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Waiting for the two notifications"
                        ) {
                            // Two promotions have been done!
                            ontrack.notifications.inMemory.group(group).size == 2
                        }

                        // Getting the last notification
                        val message = ontrack.notifications.inMemory.group(group).last()
                        assertEquals(
                            """
                                Version 1.2.0 has been released.
                                
                                Issues:
                                
                                * ISS-21 Some new feature
                                * ISS-22 Some fixes are needed
                                * ISS-23 Some nicer UI
                                
                                Documentation:
                                
                                * Updating the readme
                                
                                Misc.:
                                
                                * Formatting some code
                            """.trimIndent(),
                            message
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Sending a semantic change log on a promotion run with some issues and adapting title`() {
        val group = uid("group-")
        withMockScmRepository(ontrack) {
            project {
                branch {
                    configuredForMockScm()
                    val pl = promotion()
                    // Subscribe to this promotion
                    pl.subscribe(
                        name = "Test",
                        channel = "in-memory",
                        channelConfig = mapOf("group" to group),
                        keywords = null,
                        events = listOf(
                            "new_promotion_run",
                        ),
                        contentTemplate = $$"""
                            Version ${build.release} has been released.
                            
                            ${promotionRun.semanticChangelog?issues=true&section=ci=Delivery&section=chore=Other}
                        """.trimIndent()
                    )

                    build {}
                    build {
                        release = "1.1.0"

                        // Mock termination commit
                        repositoryIssue("ISS-20", "Last issue before the change log")
                        withRepositoryCommit("ISS-20 Last commit before the change log")

                        promote(pl.name)  // This sends also a notification!
                    }
                    build {
                        repositoryIssue("ISS-21", "Some new feature")
                        withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                        withRepositoryCommit("ISS-21 Some fixes for a feature")
                        withRepositoryCommit("ci: Fixing the pipeline")
                        withRepositoryCommit("chore: Formatting some code")
                    }
                    build {
                        repositoryIssue("ISS-22", "Some fixes are needed")
                        withRepositoryCommit("ISS-22 Fixing some bugs")
                        withRepositoryCommit("doc: Updating the readme")
                    }
                    build {
                        release = "1.2.0"
                        repositoryIssue("ISS-23", "Some nicer UI")
                        withRepositoryCommit("ISS-23 Fixing some CSS")

                        promote(pl.name)

                        waitUntil(
                            timeout = 30_000,
                            interval = 500L,
                            task = "Waiting for the two notifications"
                        ) {
                            // Two promotions have been done!
                            ontrack.notifications.inMemory.group(group).size == 2
                        }

                        // Getting the last notification
                        val message = ontrack.notifications.inMemory.group(group).last()
                        assertEquals(
                            """
                                Version 1.2.0 has been released.
                                
                                Issues:
                                
                                * ISS-21 Some new feature
                                * ISS-22 Some fixes are needed
                                * ISS-23 Some nicer UI
                                
                                Delivery:
                                
                                * Fixing the pipeline
                                
                                Documentation:
                                
                                * Updating the readme
                                
                                Other:
                                
                                * Formatting some code
                            """.trimIndent(),
                            message
                        )
                    }
                }
            }
        }
    }

}