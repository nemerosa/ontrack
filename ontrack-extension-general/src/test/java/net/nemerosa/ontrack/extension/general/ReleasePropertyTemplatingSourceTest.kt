package net.nemerosa.ontrack.extension.general

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReleasePropertyTemplatingSourceTest {

    private lateinit var releasePropertyTemplatingSource: ReleasePropertyTemplatingSource
    private lateinit var propertyService: PropertyService

    @BeforeEach
    fun init() {
        propertyService = mockk()
        releasePropertyTemplatingSource = ReleasePropertyTemplatingSource(
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
        assertEquals(
            "1.0.0",
            releasePropertyTemplatingSource.render(build, emptyMap(), PlainEventRenderer.INSTANCE)
        )
    }

    @Test
    fun `No extension if no property`() {
        val build = BuildFixtures.testBuild()
        every { propertyService.getPropertyValue(build, ReleasePropertyType::class.java) } returns null
        assertEquals(
            "",
            releasePropertyTemplatingSource.render(build, emptyMap(), PlainEventRenderer.INSTANCE)
        )
    }

    @Test
    fun `No extension if not a build`() {
        val build = BranchFixtures.testBranch()
        assertEquals(
            "",
            releasePropertyTemplatingSource.render(build, emptyMap(), PlainEventRenderer.INSTANCE)
        )
    }

}