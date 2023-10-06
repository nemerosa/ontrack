package net.nemerosa.ontrack.extension.av.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.BranchFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AutoVersioningBranchExpressionServiceImplTest {

    private lateinit var autoVersioningBranchExpressionService: AutoVersioningBranchExpressionService
    private lateinit var branchSource: BranchSource
    private lateinit var branchSourceFactory: BranchSourceFactory

    @BeforeEach
    fun init() {
        branchSource = mockk()

        branchSourceFactory = mockk()
        every { branchSourceFactory.getBranchSource("same") } returns branchSource

        autoVersioningBranchExpressionService = AutoVersioningBranchExpressionServiceImpl(
            branchSourceFactory = branchSourceFactory,
        )
    }

    @Test
    fun `Using &same on the same branch`() {
        val eligibleTargetBranch = BranchFixtures.testBranch()
        val anySourceBranch = BranchFixtures.testBranch(name = eligibleTargetBranch.name)

        every {
            branchSource.getLatestBranch(
                null,
                anySourceBranch.project,
                eligibleTargetBranch
            )
        } returns anySourceBranch

        val latestBranch =
            autoVersioningBranchExpressionService.getLatestBranch(eligibleTargetBranch, anySourceBranch.project, "same")
        assertEquals(anySourceBranch, latestBranch, "Using the same branch")
    }

    @Test
    fun `Using &same not on the same branch`() {
        val eligibleTargetBranch = BranchFixtures.testBranch()
        val anySourceBranch = BranchFixtures.testBranch()

        every { branchSource.getLatestBranch(null, anySourceBranch.project, eligibleTargetBranch) } returns null

        val latestBranch =
            autoVersioningBranchExpressionService.getLatestBranch(eligibleTargetBranch, anySourceBranch.project, "same")
        assertNull(latestBranch, "Not the same branch")
    }

}