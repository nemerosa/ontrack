package net.nemerosa.ontrack.extension.github.autoversioning

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationService
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AutoVersioningCheckEventProcessorTest {

    private lateinit var ingestionModelAccessService: IngestionModelAccessService
    private lateinit var autoVersioningValidationService: AutoVersioningValidationService
    private lateinit var processor: AutoVersioningCheckEventProcessor

    @BeforeEach
    fun init() {
        ingestionModelAccessService = mockk()
        autoVersioningValidationService = mockk()
        processor = AutoVersioningCheckEventProcessor(
            ingestionModelAccessService = ingestionModelAccessService,
            autoVersioningValidationService = autoVersioningValidationService,
        )
    }

    @Test
    fun `Pre processing`() {
        assertEquals(IngestionEventPreprocessingCheck.TO_BE_PROCESSED, processor.preProcessingCheck(samplePayload()))
    }

    @Test
    fun `Processing without any validation`() {
        val build = Build.of(
            Branch.of(
                Project.of(NameDescription.nd("prj", "")),
                NameDescription.nd("main", "")
            ),
            NameDescription.nd("1", ""),
            Signature.of("test")
        )
        every {
            ingestionModelAccessService.findBuildByRunId(
                repository = Repository.stub("nemerosa", "my-repo"),
                runId = 1L,
            )
        } returns build

        every {
            autoVersioningValidationService.checkAndValidate(build)
        } returns emptyList()

        val payload = samplePayload()

        assertEquals(IngestionEventProcessingResult.IGNORED, processor.process(payload, null).result)
    }

    @Test
    fun `Processing with validation`() {
        val build = Build.of(
            Branch.of(
                Project.of(NameDescription.nd("prj", "")),
                NameDescription.nd("main", "")
            ),
            NameDescription.nd("1", ""),
            Signature.of("test")
        )
        every {
            ingestionModelAccessService.findBuildByRunId(
                repository = Repository.stub("nemerosa", "my-repo"),
                runId = 1L,
            )
        } returns build

        every {
            autoVersioningValidationService.checkAndValidate(build)
        } returns listOf(
            mockk()
        )

        val payload = samplePayload()

        assertEquals(IngestionEventProcessingResult.PROCESSED, processor.process(payload, null).result)
    }

    private fun samplePayload() = AutoVersioningCheckDataPayload(
        owner = "nemerosa",
        repository = "my-repo",
        runId = 1L,
    )

}