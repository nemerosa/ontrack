package net.nemerosa.ontrack.extension.av.processing

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class BackValidationAutoVersioningCompletionListenerTest {

    private lateinit var structureService: StructureService
    private lateinit var listener: BackValidationAutoVersioningCompletionListener

    @BeforeEach
    fun init() {
        structureService = mockk(relaxed = true)
        listener = BackValidationAutoVersioningCompletionListener(structureService)
    }

    @Test
    fun `No back validation when back validation parameter is not set`() {
        val source = BuildFixtures.testBuild()
        val branch = BranchFixtures.testBranch()
        val order = branch.createOrder(sourceProject = source.project.name)
        listener.onAutoVersioningCompletion(order, AutoVersioningProcessingOutcome.CREATED)
        verify {
            structureService wasNot Called
        }
    }

    @Test
    fun `No back validation when source build ID parameter is not set`() {
        val source = BuildFixtures.testBuild()
        val branch = BranchFixtures.testBranch()
        val order = branch.createOrder(
            sourceProject = source.project.name,
            sourceBackValidation = "back-validation"
        )
        listener.onAutoVersioningCompletion(order, AutoVersioningProcessingOutcome.CREATED)
        verify {
            structureService wasNot Called
        }
    }

    @Test
    fun `No back validation when source build ID cannot be found`() {

        val source = BuildFixtures.testBuild()
        val unknownBuildId = source.id() + 1
        val branch = BranchFixtures.testBranch()
        val order = branch.createOrder(
            sourceProject = source.project.name,
            sourceBackValidation = "back-validation",
            sourceBuildId = unknownBuildId,
        )

        every { structureService.findBuildByID(ID.of(unknownBuildId)) } returns null

        listener.onAutoVersioningCompletion(order, AutoVersioningProcessingOutcome.CREATED)
        verify(exactly = 0) {
            structureService.newValidationRun(source, any())
        }
    }

    @Test
    fun `Back validation with passed outcome`() {

        val source = BuildFixtures.testBuild()
        val vs = ValidationStampFixtures.testValidationStamp(branch = source.branch, name = "back-validation")
        val branch = BranchFixtures.testBranch()
        val order = branch.createOrder(
            sourceProject = source.project.name,
            sourceBackValidation = vs.name,
            sourceBuildId = source.id(),
        )

        every { structureService.findBuildByID(source.id) } returns source
        every { structureService.findValidationStampByName(source.project.name, source.branch.name, vs.name)} returns Optional.of(vs)

        listener.onAutoVersioningCompletion(order, AutoVersioningProcessingOutcome.CREATED)
        verify {
            structureService.newValidationRun(
                source,
                match { request: ValidationRunRequest ->
                    request.validationStampName == "back-validation" &&
                            request.validationRunStatusId == ValidationRunStatusID.STATUS_PASSED
                }
            )
        }
    }

    @Test
    fun `Back validation with failed outcome`() {

        listOf(
            AutoVersioningProcessingOutcome.SAME_VERSION,
            AutoVersioningProcessingOutcome.TIMEOUT,
            AutoVersioningProcessingOutcome.NO_CONFIG,
        ).forEach { outcome ->

            val source = BuildFixtures.testBuild()
            val vs = ValidationStampFixtures.testValidationStamp(branch = source.branch, name = "back-validation")
            val branch = BranchFixtures.testBranch()
            val order = branch.createOrder(
                sourceProject = source.project.name,
                sourceBackValidation = vs.name,
                sourceBuildId = source.id(),
            )

            every { structureService.findBuildByID(source.id) } returns source
            every { structureService.findValidationStampByName(source.project.name, source.branch.name, vs.name)} returns Optional.of(vs)

            listener.onAutoVersioningCompletion(order, outcome)
            verify {
                structureService.newValidationRun(
                    source,
                    match { request: ValidationRunRequest ->
                        request.validationStampName == "back-validation" &&
                                request.validationRunStatusId == ValidationRunStatusID.STATUS_FAILED
                    }
                )
            }
        }
    }

}