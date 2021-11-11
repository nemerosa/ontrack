package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import org.junit.Test
import kotlin.test.assertEquals

class IngestionConfigTest {

    @Test
    fun `Job validation stamp name without customization`() {
        assertEquals(
            "job",
            IngestionConfig().getValidationStampName("job", null)
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
            ).getValidationStampName("job", null)
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
            ).getValidationStampName("job", null)
        )
    }

    @Test
    fun `Step validation stamp name without customization`() {
        assertEquals(
            "job-name-step-name",
            IngestionConfig(
                steps = listOf(
                    StepConfig(name = "Step name"),
                )
            ).getValidationStampName("Job name", "Step name")
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
            ).getValidationStampName("Job name", "Step name")
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
            ).getValidationStampName("Job name", "Step name")
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
            ).getValidationStampName("Job name", "Step name")
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
            ).getValidationStampName("Job name", "Step name")
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
}