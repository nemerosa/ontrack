package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureService
import net.nemerosa.ontrack.extension.github.ingestion.queue.AsyncIngestionHookQueue
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration test using RabbitMQ.
 */
@TestPropertySource(
    properties = [
        // Overriding the settings in AbstractIngestionTestSupport
        "ontrack.extension.github.ingestion.queue.async=true",
    ]
)
class IngestionHookRabbitMQIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var ingestionConfigProperties: IngestionConfigProperties

    @Autowired
    private lateinit var ingestionHookQueue: IngestionHookQueue

    @Autowired
    private lateinit var ingestionHookController: IngestionHookController

    @Autowired
    private lateinit var platformTransactionManager: PlatformTransactionManager

    private lateinit var transactionTemplate: TransactionTemplate

    @Before
    fun before() {
        transactionTemplate = TransactionTemplate(platformTransactionManager).apply {
            propagationBehavior = PROPAGATION_REQUIRES_NEW
        }
    }

    @Test
    fun `Configuration check`() {
        assertTrue(ingestionConfigProperties.queue.async, "Running in async mode")
        assertIs<AsyncIngestionHookQueue>(ingestionHookQueue, "Running in async mode")
    }

    @Test
    fun `Default processing of a workflow run event`() {
        // Preparing a test payload
        val name = uid("r")
        val body = IngestionHookFixtures.sampleWorkflowRunJsonPayload(
            repoName = name,
        ).format()
        val headers = IngestionHookFixtures.payloadHeaders(event = "workflow_run")
        // GitHub configuration (needed for the GitHub config to be available in a separate transaction)
        transactionTemplate.execute {
            onlyOneGitHubConfig()
        }
        // Sending the event
        ingestionHookController.hook(
            body = body,
            gitHubDelivery = headers.gitHubDelivery,
            gitHubEvent = headers.gitHubEvent,
            gitHubHookID = headers.gitHubHookID,
            gitHubHookInstallationTargetID = headers.gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = headers.gitHubHookInstallationTargetType,
            signature = "", // Not checking the signature for testing
        )
        // Need to wait until the event has been processed (async processing here!)
        waitUntilIngestion(
            statuses = listOf(IngestionHookPayloadStatus.COMPLETED),
            gitHubEvent = "workflow_run",
            repository = name,
        )
        // Checking the corresponding items have been created
        asAdmin {
            assertNotNull(
                structureService.findProjectByName(name).getOrNull(),
                "Project has been created"
            ) { project ->
                assertNotNull(
                    structureService.findBranchByName(project.name, IngestionHookFixtures.sampleBranch).getOrNull(),
                    "Branch has been created"
                ) { branch ->
                    assertNotNull(
                        structureService.findBuildByName(branch.project.name, branch.name, "ci-1").getOrNull(),
                        "Build has been created"
                    )
                }
            }
        }
    }

    @Configuration
    class IngestionHookRabbitMQITConfig {
        /**
         * Not checking the signature of the payload
         */
        @Bean
        @Primary
        fun ingestionHookSignatureService(): IngestionHookSignatureService = MockIngestionHookSignatureService()
    }

}