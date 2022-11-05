package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IngestionConfigTest {

    @Test
    fun `Job validation stamp name without customization`() {
        assertEquals(
            "job",
            IngestionConfig().getValidationStampName(settings(), "job", null)
        )
    }

    @Test
    fun `Job validation stamp name with customization without validation`() {
        assertEquals(
            "job",
            IngestionConfig(
                jobs = listOf(
                    JobConfig(name = "job"),
                )
            ).getValidationStampName(settings(), "job", null)
        )
    }

    @Test
    fun `Job validation stamp name with customization with validation`() {
        assertEquals(
            "job-validation",
            IngestionConfig(
                jobs = listOf(
                    JobConfig(name = "job", validation = "job-validation"),
                )
            ).getValidationStampName(settings(), "job", null)
        )
    }

    @Test
    fun `Step validation stamp name without customization`() {
        assertEquals(
            "job-name-step-name",
            IngestionConfig().getValidationStampName(settings(), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name with customization of validation`() {
        assertEquals(
            "job-name-step-validation",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name", validation = "step-validation"),
                )
            ).getValidationStampName(settings(), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name with customization of validation and job name`() {
        assertEquals(
            "job-validation-step-validation",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name", validation = "step-validation"),
                ),
                jobs = listOf(
                    JobConfig(name = "Job name", validation = "job-validation")
                ),
            ).getValidationStampName(settings(), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name with customization of validation and no job prefix`() {
        assertEquals(
            "step-validation",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name", validation = "step-validation", validationJobPrefix = false),
                )
            ).getValidationStampName(settings(), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name with customization of job prefix`() {
        assertEquals(
            "step-name",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name", validationJobPrefix = false),
                )
            ).getValidationStampName(settings(), "Job name", "Step name")
        )
    }

    @Test
    fun `Job validation stamp description without customization`() {
        assertEquals(
            "Job name",
            IngestionConfig().getValidationStampDescription("Job name", null)
        )
    }

    @Test
    fun `Job validation stamp description with customization but no description`() {
        assertEquals(
            "Job name",
            IngestionConfig(
                jobs = listOf(
                    JobConfig(name = "Job name"),
                )
            ).getValidationStampDescription("Job name", null)
        )
    }

    @Test
    fun `Job validation stamp description with customization of description`() {
        assertEquals(
            "Job description",
            IngestionConfig(
                jobs = listOf(
                    JobConfig(name = "Job name", description = "Job description"),
                )
            ).getValidationStampDescription("Job name", null)
        )
    }

    @Test
    fun `Step validation stamp description without customization`() {
        assertEquals(
            "Step name",
            IngestionConfig().getValidationStampDescription("Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp description with customization but no description`() {
        assertEquals(
            "Step name",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name"),
                )
            ).getValidationStampDescription("Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp description with customization of description`() {
        assertEquals(
            "Step description",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name", description = "Step description"),
                )
            ).getValidationStampDescription("Job name", "Step name")
        )
    }

    @Test
    fun `Filter on jobs`() {
        assertTrue(IngestionConfig().filterJob("My job"))
        assertTrue(IngestionConfig(jobsFilter = FilterConfig(includes = "My.*")).filterJob("My job"))
        assertFalse(IngestionConfig(jobsFilter = FilterConfig(excludes = "My.*")).filterJob("My job"))
    }

    @Test
    fun `Filter on steps`() {
        assertTrue(IngestionConfig().filterStep("My step"))
        assertTrue(IngestionConfig(stepsFilter = FilterConfig(includes = "My.*")).filterStep("My step"))
        assertFalse(IngestionConfig(stepsFilter = FilterConfig(excludes = "My.*")).filterStep("My step"))
    }

    @Test
    fun `Step validation stamp name using defaults in settings to add the job prefix`() {
        assertEquals(
            "job-name-step-name",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name"),
                )
            ).getValidationStampName(settings(validationJobPrefix = true), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name using defaults in settings to remove the job prefix`() {
        assertEquals(
            "step-name",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name"),
                )
            ).getValidationStampName(settings(validationJobPrefix = false), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name using defaults in general config to add the job prefix`() {
        assertEquals(
            "job-name-step-name",
            IngestionConfig(
                general = IngestionConfigGeneral(
                    validationJobPrefix = true
                ),
                steps = listOf(
                    StepConfig(name = "Step name"),
                )
            ).getValidationStampName(settings(validationJobPrefix = false), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name using defaults in job config to add the job prefix`() {
        assertEquals(
            "job-name-step-name",
            IngestionConfig(
                general = IngestionConfigGeneral(
                    validationJobPrefix = false
                ),
                steps = listOf(
                    StepConfig(name = "Step name"),
                ),
                jobs = listOf(
                    JobConfig(name = "Job name", validationJobPrefix = true)
                )
            ).getValidationStampName(settings(validationJobPrefix = false), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name using step config to add the job prefix`() {
        assertEquals(
            "job-name-step-name",
            IngestionConfig(
                general = IngestionConfigGeneral(
                    validationJobPrefix = false
                ),
                steps = listOf(
                    StepConfig(name = "Step name", validationJobPrefix = true),
                ),
                jobs = listOf(
                    JobConfig(name = "Job name", validationJobPrefix = false)
                )
            ).getValidationStampName(settings(validationJobPrefix = false), "Job name", "Step name")
        )
    }

    @Test
    fun `Step validation stamp name using step config to remove the job prefix`() {
        assertEquals(
            "step-name",
            IngestionConfig(
                general = IngestionConfigGeneral(
                    validationJobPrefix = true
                ),
                steps = listOf(
                    StepConfig(name = "Step name", validationJobPrefix = false),
                ),
                jobs = listOf(
                    JobConfig(name = "Job name", validationJobPrefix = true)
                )
            ).getValidationStampName(settings(validationJobPrefix = true), "Job name", "Step name")
        )
    }

    private fun settings(
        validationJobPrefix: Boolean = true,
    ) = GitHubIngestionSettings(
        token = "some-token",
        validationJobPrefix = validationJobPrefix,
    )
}