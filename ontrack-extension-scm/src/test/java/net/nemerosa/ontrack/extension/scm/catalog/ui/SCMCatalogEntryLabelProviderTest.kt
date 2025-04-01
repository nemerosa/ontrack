package net.nemerosa.ontrack.extension.scm.catalog.ui

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SCMCatalogEntryLabelProviderTest {

    private lateinit var catalogLinkService: CatalogLinkService

    private lateinit var provider: SCMCatalogEntryLabelProvider

    private lateinit var project: Project

    @BeforeEach
    fun init() {
        project = Project.of(NameDescription.nd("PRJ", "")).withId(ID.of(1))
        catalogLinkService = mockk(relaxed = true)
        provider = SCMCatalogEntryLabelProvider(catalogLinkService)
    }

    @Test
    fun properties() {
        assertTrue(provider.isEnabled)
        assertEquals("SCM Catalog Entry", provider.name)
    }

    @Test
    fun `Label for linked project`() {
        every { catalogLinkService.getSCMCatalogEntry(project) } returns CatalogFixtures.entry()
        val labels = provider.getLabelsForProject(project)
        assertEquals(1, labels.size)
        val label = labels.first()
        with(label) {
            assertEquals("scm-catalog", category)
            assertEquals("entry", name)
            assertEquals("#33cc33", color)
        }
    }

    @Test
    fun `Label for unlinked project`() {
        every { catalogLinkService.getSCMCatalogEntry(project) } returns null
        val labels = provider.getLabelsForProject(project)
        assertEquals(1, labels.size)
        val label = labels.first()
        with(label) {
            assertEquals("scm-catalog", category)
            assertEquals("no-entry", name)
            assertEquals("#a9a9a9", color)
        }
    }

}