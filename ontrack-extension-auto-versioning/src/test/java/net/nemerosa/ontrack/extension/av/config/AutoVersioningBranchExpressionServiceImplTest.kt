package net.nemerosa.ontrack.extension.av.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class AutoVersioningBranchExpressionServiceImplTest {

    private lateinit var structureService: StructureService
    private lateinit var autoVersioningBranchExpressionService: AutoVersioningBranchExpressionService

    @BeforeEach
    fun init() {
        structureService = mockk()
        autoVersioningBranchExpressionService = AutoVersioningBranchExpressionServiceImpl(
            structureService = structureService,
        )
    }

    @Test
    fun `Using &same on the same branch`() {
        val eligibleTargetBranch = BranchFixtures.testBranch()
        val anySourceBranch = BranchFixtures.testBranch(name = eligibleTargetBranch.name)
        every { structureService.findBranchByName(anySourceBranch.project.name, eligibleTargetBranch.name) } returns Optional.of(anySourceBranch)
        val latestBranch = autoVersioningBranchExpressionService.getLatestBranch(eligibleTargetBranch, anySourceBranch.project, "same")
        assertEquals(anySourceBranch, latestBranch, "Using the same branch")
    }

    @Test
    fun `Using &same not on the same branch`() {
        val eligibleTargetBranch = BranchFixtures.testBranch()
        val anySourceBranch = BranchFixtures.testBranch()
        every { structureService.findBranchByName(anySourceBranch.project.name, eligibleTargetBranch.name) } returns Optional.empty()

        val latestBranch = autoVersioningBranchExpressionService.getLatestBranch(eligibleTargetBranch, anySourceBranch.project, "same")
        assertNull(latestBranch, "Not the same branch")
    }

    @Test
    fun `Cannot parse expression`() {
        val eligibleTargetBranch = BranchFixtures.testBranch()
        val anySourceBranch = BranchFixtures.testBranch()
        assertFailsWith<AutoVersioningBranchExpressionParsingException> {
            autoVersioningBranchExpressionService.getLatestBranch(eligibleTargetBranch, anySourceBranch.project, "not-valid")
        }
    }

}