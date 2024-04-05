package net.nemerosa.ontrack.extension.av.dispatcher

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DefaultVersionSourceTest {

    private lateinit var buildDisplayNameService: BuildDisplayNameService
    private lateinit var structureService: StructureService
    private lateinit var source: VersionSource

    @BeforeEach
    fun init() {
        buildDisplayNameService = mockk()
        structureService = mockk()
        source = DefaultVersionSource(buildDisplayNameService)
    }

    @Test
    fun `Build name present`() {
        val build = BuildFixtures.testBuild()
        val name = uid("v_")
        every { buildDisplayNameService.getEligibleBuildDisplayName(build) } returns name
        val version = source.getVersion(build, null)
        assertEquals(name, version)
    }

    @Test
    fun `Build name not present`() {
        val build = BuildFixtures.testBuild()
        every { buildDisplayNameService.getEligibleBuildDisplayName(build) } returns null
        assertFailsWith<VersionSourceNoVersionException> {
            source.getVersion(build, null)
        }
    }

}