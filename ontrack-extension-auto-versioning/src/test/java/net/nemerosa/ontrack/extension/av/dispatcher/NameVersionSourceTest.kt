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

class NameVersionSourceTest {

    private lateinit var structureService: StructureService
    private lateinit var source: VersionSource

    @BeforeEach
    fun init() {
        structureService = mockk()
        source = NameVersionSource(structureService)
    }

    @Test
    fun `Build name`() {
        val build = BuildFixtures.testBuild()
        val version = source.getVersion(build, null)
        assertEquals(build.name, version)
    }

}