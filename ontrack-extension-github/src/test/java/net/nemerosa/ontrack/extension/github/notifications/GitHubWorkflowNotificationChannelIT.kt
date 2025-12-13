package net.nemerosa.ontrack.extension.github.notifications

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.githubTestConfigReal
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordFilter
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.it.waitUntil
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@TestOnGitHub
@QueueNoAsync
class GitHubWorkflowNotificationChannelIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var ontrackGitHubClientFactory: OntrackGitHubClientFactory

    @Autowired
    private lateinit var eventSubscriptionService: EventSubscriptionService

    @Autowired
    private lateinit var gitHubWorkflowNotificationChannel: GitHubWorkflowNotificationChannel

    @Autowired
    private lateinit var notificationRecordingService: NotificationRecordingService

    @OptIn(ExperimentalTime::class)
    @Test
    @AsAdminTest
    fun `GitHub workflow notification channel`() {
        // Creating a GitHub config
        val gitConfiguration = githubTestConfigReal()
        gitConfigurationService.newConfiguration(gitConfiguration)

        // Setup
        project {
            branch {
                val gold = promotionLevel("GOLD")
                eventSubscriptionService.subscribe(
                    name = "On GOLD",
                    channel = gitHubWorkflowNotificationChannel,
                    channelConfig = GitHubWorkflowNotificationChannelConfig(
                        config = gitConfiguration.name,
                        owner = githubTestEnv.organization,
                        repository = githubTestEnv.repository,
                        workflowId = githubTestEnv.actions.workflowId,
                        reference = githubTestEnv.actions.branch,
                        inputs = listOf(
                            GitHubWorkflowNotificationChannelConfigInput("text", "Some text"),
                        ),
                        callMode = GitHubWorkflowNotificationChannelConfigCallMode.SYNC,
                        timeoutSeconds = 120,
                    ),
                    projectEntity = gold,
                    keywords = null,
                    origin = "test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN,
                )

                build {
                    promote(gold)

                    // We expect a record of the notification
                    var workflowRunId: Long? = null
                    waitUntil(
                        message = "Notification recorded",
                        timeout = 120.seconds,
                    ) {
                        val records = notificationRecordingService.filter(
                            filter = NotificationRecordFilter(
                                eventEntityId = gold.toProjectEntityID(),
                            )
                        ).pageItems
                        if (records.isNotEmpty()) {
                            val record = records.first()
                            assertNotNull(record.result.output, "Output was recorded") { output ->
                                val ghOutput: GitHubWorkflowNotificationChannelOutput = output.parse()
                                workflowRunId = ghOutput.workflowRunId
                            }
                            // OK, found it
                            true
                        } else {
                            false
                        }
                    }

                    val finalWorkflowRunId = workflowRunId
                    assertNotNull(finalWorkflowRunId, "Notification recorded with workflow run ID")

                    // We expect the workflow to have been fired
                    val client = ontrackGitHubClientFactory.create(gitConfiguration)
                    waitUntil(
                        message = "Workflow done",
                        timeout = 120.seconds,
                    ) {
                        val run = client.getWorkflowRun(
                            repository = "${githubTestEnv.organization}/${githubTestEnv.repository}",
                            runId = finalWorkflowRunId,
                        )
                        run.success == true
                    }
                }
            }
        }
    }

}