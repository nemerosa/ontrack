package net.nemerosa.ontrack.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.support.CoreExtensionFeature
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DecorationServiceImplTest {

    @Test
    fun error_decoration_generates_default_error_decoration() {
        val projectEntity = mockk<ProjectEntity>()
        every { projectEntity.entityDisplayName } returns "project"

        val decorator = mockk<DecorationExtension<*>>()
        every {
            decorator.getDecorations(any())
        } throws RuntimeException("Error while generating the decoration")
        every {
            decorator.feature
        } returns CoreExtensionFeature()

        val extensionManager = mockk<ExtensionManager>()
        every {
            extensionManager.getExtensions<DecorationExtension<*>>(DecorationExtension::class.java)
        } returns listOf(decorator)

        val securityService = MockSecurityService()

        val service = DecorationServiceImpl(extensionManager, securityService)

        val decorations = service.getDecorations(projectEntity, decorator)
        val decoration = decorations.single()
        assertNull(decoration.data)
        assertEquals("Problem while getting decoration", decoration.error)
    }

}
