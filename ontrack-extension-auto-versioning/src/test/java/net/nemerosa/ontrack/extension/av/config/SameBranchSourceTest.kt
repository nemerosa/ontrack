package net.nemerosa.ontrack.extension.av.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class SameBranchSourceTest {

    private lateinit var structureService: StructureService
    private lateinit var source: SameBranchSource

    @BeforeEach
    fun init() {
        structureService = mockk()
        source = SameBranchSource(structureService)
    }

    @Test
    fun `Same branch being present`() {
        val name = uid("b_")
        val dependency = BranchFixtures.testBranch(name = name)
        val parent = BranchFixtures.testBranch(name = name) // Same name

        every { structureService.findBranchByName(dependency.project.name, dependency.name) } returns Optional.of(dependency)

        val latest = source.getLatestBranch(null, dependency.project, parent, "ANY")
        assertEquals(dependency, latest)
    }

    @Test
    fun `Same branch being not present`() {
        val dependency = BranchFixtures.testBranch()
        val parent = BranchFixtures.testBranch() // Not the same name

        every { structureService.findBranchByName(dependency.project.name, dependency.name) } returns Optional.empty()

        val latest = source.getLatestBranch(null, dependency.project, parent, "ANY")
        assertEquals(null, latest)
    }

}