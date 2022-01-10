package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestJUnit4Support
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Testing the auto promotion setup.
 */
@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
class AutoPromotionIT : AbstractIngestionTestJUnit4Support() {

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @Autowired
    private lateinit var workflowRunIngestionEventProcessor: WorkflowRunIngestionEventProcessor

    @Autowired
    private lateinit var workflowJobIngestionEventProcessor: WorkflowJobIngestionEventProcessor

    private val ref = Time.now()
    private val job = "job"

    @Test
    fun `Auto promotion`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Auto promotion setup to be loaded for the repository
        ConfigLoaderServiceITMockConfig.customIngestionConfig(
            configLoaderService,
            IngestionConfig(
                promotions = listOf(
                    PromotionConfig(
                        name = "BRONZE",
                        validations = listOf("$job-build", "$job-unit-test"),
                    ),
                    PromotionConfig(
                        name = "SILVER",
                        validations = listOf("$job-integration-test"),
                        promotions = listOf("BRONZE"),
                    ),
                )
            )
        )
        asAdmin {
            // Starting the run
            startRun()
            // Checks that validations & promotions are created, and that auto promotion is set up
            checkValidationStamps()
            checkPromotionLevels()
            // Job runs for the 'BRONZE' level
            runJobs("build", "unit-test")
            // Checks that 'BRONZE' has been granted
            checkValidations("$job-build", "$job-unit-test")
            checkPromotion("BRONZE")
            // Job runs for the 'SILVER' level
            runJobs("integration-test")
            // Checks that 'SILVER' has been granted
            checkValidations("$job-integration-test")
            checkPromotion("SILVER")
        }
    }

    private fun startRun() {
        workflowRunIngestionEventProcessor.process(
            IngestionHookFixtures.sampleWorkflowRunPayload(), null
        )
    }

    private fun checkValidationStamps() {
        val build = getBuild()
        listOf("build", "unit-test", "integration-test").forEach { vsName ->
            val vs =
                structureService.findValidationStampByName(build.project.name, build.branch.name, "$job-$vsName").getOrNull()
            assertNotNull(vs, "Validation stamp $job-$vsName has been created.")
        }
    }

    private fun checkPromotionLevels() {
        val build = getBuild()
        assertNotNull(
            structureService.findPromotionLevelByName(build.project.name, build.branch.name, "BRONZE")
                .getOrNull()
        ) { pl ->
            assertNotNull(getProperty(pl, AutoPromotionPropertyType::class.java)) { property ->
                assertEquals(
                    setOf("$job-build", "$job-unit-test"),
                    property.validationStamps.map { it.name }.toSet()
                )
                assertTrue(property.promotionLevels.isEmpty())
            }
        }
        assertNotNull(
            structureService.findPromotionLevelByName(build.project.name, build.branch.name, "SILVER")
                .getOrNull()
        ) { pl ->
            assertNotNull(getProperty(pl, AutoPromotionPropertyType::class.java)) { property ->
                assertEquals(
                    setOf("$job-integration-test"),
                    property.validationStamps.map { it.name }.toSet()
                )
                assertEquals(
                    listOf("BRONZE"),
                    property.promotionLevels.map { it.name }
                )
            }
        }
    }

    private fun runJobs(vararg validations: String) {
        workflowJobIngestionEventProcessor.process(
            WorkflowJobPayload(
                action = WorkflowJobAction.in_progress,
                workflowJob = WorkflowJob(
                    runId = 1,
                    runAttempt = 1,
                    status = WorkflowJobStepStatus.in_progress,
                    conclusion = null,
                    startedAtDate = ref,
                    completedAtDate = null,
                    name = job,
                    steps = validations.map { validation ->
                        WorkflowJobStep(
                            name = validation,
                            status = WorkflowJobStepStatus.completed,
                            conclusion = WorkflowJobStepConclusion.success,
                            number = 1,
                            startedAtDate = ref,
                            completedAtDate = ref.plusSeconds(10),
                        )
                    },
                    htmlUrl = "",
                ),
                repository = IngestionHookFixtures.sampleRepository(),
            ),
            null
        )
    }

    private fun checkValidations(vararg validations: String) {
        val build = getBuild()
        validations.forEach { validation ->
            val vs = structureService.findValidationStampByName(build.project.name, build.branch.name, validation)
                .getOrNull()
                ?: fail("Cannot find validation $validation")
            val runs = structureService.getValidationRunsForBuildAndValidationStamp(
                build.id,
                vs.id,
                0,
                10
            )
            assertTrue(
                runs.any { it.lastStatus.statusID == ValidationRunStatusID.STATUS_PASSED },
                "Build has been validated for $validation"
            )
        }
    }

    private fun checkPromotion(promotion: String) {
        val build = getBuild()
        val pl = structureService.findPromotionLevelByName(build.project.name, build.branch.name, promotion)
            .getOrNull()
            ?: fail("Cannot find promotion $promotion")
        val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, pl)
        assertTrue(runs.isNotEmpty(), "Build has been promoted to $promotion")
    }

    private fun getBuild() =
        structureService.findBuildByName(
            IngestionHookFixtures.sampleRepository,
            IngestionHookFixtures.sampleBranch,
            buildName
        ).getOrNull() ?: fail("Cannot find the created build")

    private val buildName: String = "${IngestionHookFixtures.sampleRunName}-1".lowercase()

}