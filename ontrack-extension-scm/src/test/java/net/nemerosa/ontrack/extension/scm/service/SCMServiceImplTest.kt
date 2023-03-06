package net.nemerosa.ontrack.extension.scm.service

import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SCMServiceImplTest {

    private val structureService = mockk<StructureService>()
    private val service = SCMUtilsServiceImpl(structureService)

    @Test
    fun `One pattern`() {
        val filter = service.getPathFilter(listOf("**/*.java"))
        assertTrue(filter.test("/root/package/File.java"))
        assertFalse(filter.test("/root/package/File.groovy"))
        assertFalse(filter.test("/root/package/File.sql"))
    }

    @Test
    fun `Two patterns`() {
        val filter = service.getPathFilter(listOf("**/*.java", "**/*.groovy"))
        assertTrue(filter.test("/root/package/File.java"))
        assertTrue(filter.test("/root/package/File.groovy"))
        assertFalse(filter.test("/root/package/File.sql"))
    }

    @Test
    fun `No pattern`() {
        val filter = service.getPathFilter(listOf())
        assertTrue(filter.test("/root/package/File.java"))
        assertTrue(filter.test("/root/package/File.groovy"))
        assertTrue(filter.test("/root/package/File.sql"))
    }

}
