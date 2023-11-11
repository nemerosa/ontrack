package net.nemerosa.ontrack.extension.general

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReleasePropertyEventParameterExtensionTest {

    private lateinit var releasePropertyEventParameterExtension: ReleasePropertyEventParameterExtension
    private lateinit var propertyService: PropertyService

    @BeforeEach
    fun init() {
        propertyService = mockk()
        releasePropertyEventParameterExtension = ReleasePropertyEventParameterExtension(
            mockk(),
            propertyService,
        )
    }

    @Test
    fun `Extension if property`() {
        val build = BuildFixtures.testBuild()
        every {
            propertyService.getPropertyValue(
                build,
                ReleasePropertyType::class.java
            )
        } returns ReleaseProperty("1.0.0")
        val parameters = releasePropertyEventParameterExtension.additionalTemplateParameters(build)
        assertEquals(
            mapOf(
                "buildLabel" to "1.0.0"
            ),
            parameters
        )
    }

    @Test
    fun `No extension if no property`() {
        val build = BuildFixtures.testBuild()
        every { propertyService.getPropertyValue(build, ReleasePropertyType::class.java) } returns null
        val parameters = releasePropertyEventParameterExtension.additionalTemplateParameters(build)
        assertEquals(
            emptyMap(),
            parameters
        )
    }

    @Test
    fun `No extension if not a build`() {
        val build = BuildFixtures.testBuild()
        val parameters = releasePropertyEventParameterExtension.additionalTemplateParameters(build.branch)
        assertEquals(
            emptyMap(),
            parameters
        )
    }

}