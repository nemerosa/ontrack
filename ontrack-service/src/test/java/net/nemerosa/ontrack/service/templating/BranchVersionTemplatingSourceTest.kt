package net.nemerosa.ontrack.service.templating

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.BranchNamePolicy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BranchVersionTemplatingSourceTest {

    private lateinit var source: BranchVersionTemplatingSource
    private lateinit var branchDisplayNameService: BranchDisplayNameService

    @BeforeEach
    fun setup() {
        branchDisplayNameService = mockk()
        source = BranchVersionTemplatingSource(
            branchDisplayNameService,
        )
    }

    @Test
    fun `Getting the branch version with default parameters and match using branch name`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        } returns "release-1.8"
        val text = source.render(
            entity = branch,
            configMap = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("1.8", text)
    }

    @Test
    fun `Getting the branch version with default parameters and match using branch name with several digits`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        } returns "release/99999.51"
        val text = source.render(
            entity = branch,
            configMap = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("99999.51", text)
    }

    @Test
    fun `Getting the branch version with default parameters and no match`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        } returns "main"
        val text = source.render(
            entity = branch,
            configMap = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("", text)
    }

    @Test
    fun `Getting the branch version with default parameters and no match with default value`() {
        val branch = BranchFixtures.testBranch(name = "main")
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        } returns "main"
        val text = source.render(
            entity = branch,
            configMap = mapOf(
                "default" to "xxx",
            ),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("xxx", text)
    }

    @Test
    fun `Getting the branch version with default parameters and match using branch SCM name`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        } returns "release/1.8"
        val text = source.render(
            entity = branch,
            configMap = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("1.8", text)
    }

    @Test
    fun `Getting the branch version using branch name only and matching branch name`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.NAME_ONLY)
        } returns "release-1.8"
        val text = source.render(
            entity = branch,
            configMap = mapOf(
                "policy" to "NAME_ONLY"
            ),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("1.8", text)
    }

    @Test
    fun `Getting the branch version using branch name only no match on branch name`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.NAME_ONLY)
        } returns "xxx"
        val text = source.render(
            entity = branch,
            configMap = mapOf(
                "policy" to "NAME_ONLY"
            ),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("", text)
    }

    @Test
    fun `Getting the branch version using branch SCM name only and matching`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_ONLY)
        } returns "release/1.8"
        val text = source.render(
            entity = branch,
            configMap = mapOf(
                "policy" to "DISPLAY_NAME_ONLY"
            ),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("1.8", text)
    }

    @Test
    fun `Getting the branch version using branch SCM name only and not matching`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_ONLY)
        } returns "main"
        val text = source.render(
            entity = branch,
            configMap = mapOf(
                "policy" to "DISPLAY_NAME_ONLY"
            ),
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("", text)
    }

    @Test
    fun `Getting the branch version using branch SCM name only and no SCM branch`() {
        val branch = BranchFixtures.testBranch()
        every {
            branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_ONLY)
        } throws RuntimeException("Cannot branch SCM name")
        assertFailsWith<RuntimeException> {
            source.render(
                entity = branch,
                configMap = mapOf(
                    "policy" to "DISPLAY_NAME_ONLY"
                ),
                renderer = PlainEventRenderer.INSTANCE,
            )
        }
    }

}