package net.nemerosa.ontrack.extension.general

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MetaInfoPropertyTemplatingSourceTest {

    private lateinit var metaInfoPropertyTemplatingSource: MetaInfoPropertyTemplatingSource
    private lateinit var propertyService: PropertyService

    @BeforeEach
    fun init() {
        propertyService = mockk()
        metaInfoPropertyTemplatingSource = MetaInfoPropertyTemplatingSource(
            propertyService,
        )
    }

    @Test
    fun `Getting a blank if no meta information property at all`() {
        val build = BuildFixtures.testBuild()
        every {
            propertyService.getPropertyValue(
                build,
                MetaInfoPropertyType::class.java
            )
        } returns null
        assertEquals(
            "",
            metaInfoPropertyTemplatingSource.render(
                build,
                mapOf(
                    "name" to "meta-key",
                ),
                PlainEventRenderer.INSTANCE
            )
        )
    }

    @Test
    fun `Getting a blank if no meta information property key`() {
        val build = BuildFixtures.testBuild()
        every {
            propertyService.getPropertyValue(
                build,
                MetaInfoPropertyType::class.java
            )
        } returns MetaInfoProperty(
            items = emptyList()
        )

        assertEquals(
            "",
            metaInfoPropertyTemplatingSource.render(
                build,
                mapOf(
                    "name" to "meta-key",
                ),
                PlainEventRenderer.INSTANCE
            )
        )
    }

    @Test
    fun `Getting an error if no meta information property key when configured so`() {
        val build = BuildFixtures.testBuild()
        every {
            propertyService.getPropertyValue(
                build,
                MetaInfoPropertyType::class.java
            )
        } returns MetaInfoProperty(
            items = emptyList()
        )

        assertFailsWith<MetaInfoPropertyTemplatingSourceMissingKeyException> {
            metaInfoPropertyTemplatingSource.render(
                build,
                mapOf(
                    "name" to "meta-key",
                    "error" to "true"
                ),
                PlainEventRenderer.INSTANCE
            )
        }
    }

    @Test
    fun `Getting a meta information by name`() {
        val build = BuildFixtures.testBuild()
        every {
            propertyService.getPropertyValue(
                build,
                MetaInfoPropertyType::class.java
            )
        } returns MetaInfoProperty(
            items = listOf(
                MetaInfoPropertyItem(
                    name = "meta-key",
                    value = "meta-value",
                    link = null,
                    category = null,
                )
            )
        )

        assertEquals(
            "meta-value",
            metaInfoPropertyTemplatingSource.render(
                build,
                mapOf(
                    "name" to "meta-key",
                ),
                PlainEventRenderer.INSTANCE
            )
        )
    }

    @Test
    fun `Getting a meta information link by name`() {
        val build = BuildFixtures.testBuild()
        every {
            propertyService.getPropertyValue(
                build,
                MetaInfoPropertyType::class.java
            )
        } returns MetaInfoProperty(
            items = listOf(
                MetaInfoPropertyItem(
                    name = "meta-key",
                    value = "meta-value",
                    link = "uri://some-link",
                    category = null,
                )
            )
        )

        assertEquals(
            """<a href="uri://some-link">meta-value</a>""",
            metaInfoPropertyTemplatingSource.render(
                build,
                mapOf(
                    "name" to "meta-key",
                    "link" to "true",
                ),
                HtmlNotificationEventRenderer(OntrackConfigProperties()),
            )
        )
    }

    @Test
    fun `Getting a meta information by name and category`() {
        val build = BuildFixtures.testBuild()
        every {
            propertyService.getPropertyValue(
                build,
                MetaInfoPropertyType::class.java
            )
        } returns MetaInfoProperty(
            items = listOf(
                MetaInfoPropertyItem(
                    name = "meta-key",
                    value = "meta-value-2",
                    link = null,
                    category = "my-cat",
                ),
                MetaInfoPropertyItem(
                    name = "meta-key",
                    value = "meta-value-1",
                    link = null,
                    category = null,
                ),
            )
        )

        assertEquals(
            "meta-value-2",
            metaInfoPropertyTemplatingSource.render(
                build,
                mapOf(
                    "name" to "meta-key",
                    "category" to "my-cat",
                ),
                PlainEventRenderer.INSTANCE
            )
        )
    }

}