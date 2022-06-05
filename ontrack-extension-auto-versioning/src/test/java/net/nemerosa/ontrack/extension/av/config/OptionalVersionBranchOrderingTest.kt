package net.nemerosa.ontrack.extension.av.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OptionalVersionBranchOrderingTest {

    private lateinit var branchDisplayNameService: BranchDisplayNameService
    private lateinit var ordering: OptionalVersionBranchOrdering

    private val project = Project.of(nd("P", ""))
    private val branch = Branch.of(project, nd("release-1.0", "")).withId(ID.of(100))

    @Before
    fun init() {
        branchDisplayNameService = mockk()
        ordering = OptionalVersionBranchOrdering(branchDisplayNameService)
    }

    @Test
    fun `toVersion returns a version`() {
        val o = ordering.toVersion("1.0")
        assertNotNull(o) {
            assertNotNull(it.version) { v ->
                assertEquals(1, v.major)
                assertEquals(0, v.minor)
                assertEquals(0, v.patch)
            }
        }
    }

    @Test
    fun `toVersion does not return a version`() {
        val o = ordering.toVersion("v1.0")
        assertNull(o)
    }

    @Test
    fun `Get version on a path with no match returns null`() {
        assertNull(ordering.getVersion("release/.*".toRegex(), "release-1.0"))
    }

    @Test
    fun `Get version on a path with one matched group returns a version`() {
        assertNotNull(ordering.getVersion("release/(.*)".toRegex(), "release/1.0")) {
            assertEquals("1.0.0", it.version?.toString())
        }
    }

    @Test
    fun `Get version on a path with one separator returns a version`() {
        assertNotNull(ordering.getVersion("release/.*".toRegex(), "release/1.0")) {
            assertEquals("1.0.0", it.version?.toString())
        }
    }

    @Test
    fun `Get version on a path with one separator but no version`() {
        assertNull(ordering.getVersion("release/.*".toRegex(), "release/v1.0"))
    }

    @Test
    fun `Get version on a matching path with no group and no separator`() {
        assertNull(ordering.getVersion("master".toRegex(), "master"))
    }

    @Test
    fun `Match on branch path first`() {
        every { branchDisplayNameService.getBranchDisplayName(branch) } returns "release/1.0"
        assertEquals(
                "1.0.0",
                ordering.getVersion(branch, "release/.*".toRegex()).version?.toString()
        )
    }

    @Test
    fun `Match on branch name`() {
        every { branchDisplayNameService.getBranchDisplayName(branch) } returns "release-1.0"
        assertNull(ordering.getVersion(branch, "release/.*".toRegex()).version)
    }

    @Test
    fun `Ordering based on version after separator even if no group specified`() {
        val b10 = Branch.of(project, nd("release-1.0", "")).withId(ID.of(100))
        val b01 = Branch.of(project, nd("release-0.1", "")).withId(ID.of(200))
        every { branchDisplayNameService.getBranchDisplayName(b10) } returns "release/1.0"
        every { branchDisplayNameService.getBranchDisplayName(b01) } returns "release/0.1"
        val comparator = ordering.getComparator("release/.*")
        val branch = listOf(b10, b01).minWithOrNull(comparator)?.name
        assertEquals("release-1.0", branch)
    }

    @Test
    fun `Ordering based on version in group`() {
        val b10 = Branch.of(project, nd("release-1.0", "")).withId(ID.of(100))
        val b01 = Branch.of(project, nd("release-0.1", "")).withId(ID.of(200))
        every { branchDisplayNameService.getBranchDisplayName(b10) } returns "release/1.0"
        every { branchDisplayNameService.getBranchDisplayName(b01) } returns "release/0.1"
        val comparator = ordering.getComparator("release/(.*)")
        val branch = listOf(b10, b01).minWithOrNull(comparator)?.name
        assertEquals("release-1.0", branch)
    }

    @Test
    fun `Ordering based on name if no version available`() {
        val b10 = Branch.of(project, nd("release-1.0", "")).withId(ID.of(100))
        val b01 = Branch.of(project, nd("release-0.1", "")).withId(ID.of(200))
        every { branchDisplayNameService.getBranchDisplayName(b10) } returns "release/1.0"
        every { branchDisplayNameService.getBranchDisplayName(b01) } returns "release/0.1"
        val comparator = ordering.getComparator("maintenance/.*")
        val branch = listOf(b10, b01).minWithOrNull(comparator)?.name
        // Here, because there is no match on version, 1.0 becomes the first branch because of its name only
        assertEquals("release-1.0", branch)
    }

}