package net.nemerosa.ontrack.extension.scm.catalog

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures.entry
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CatalogLinkServiceTest {

    private lateinit var catalogLinkService: CatalogLinkService
    private lateinit var scmCatalog: SCMCatalog
    private lateinit var scmCatalogProvider: SCMCatalogProvider
    private lateinit var structureService: StructureService
    private lateinit var entityDataService: EntityDataService

    private val project = Project(ID.of(1), "PRJ", "Project", false, Signature.of("test"))

    @Before
    fun setup() {
        scmCatalog = mock()

        scmCatalogProvider = mock()
        whenever(scmCatalogProvider.id).thenReturn("test")

        structureService = mock()
        entityDataService = mock()
        catalogLinkService = CatalogLinkServiceImpl(
                scmCatalog,
                listOf(scmCatalogProvider),
                structureService,
                entityDataService
        )
    }

    @Test
    fun `Linked project`() {
        // List of projects
        whenever(structureService.projectList).thenReturn(listOf(project))
        // Catalog entries
        val entry = entry()
        whenever(scmCatalog.catalogEntries).thenReturn(sequenceOf(entry))
        // Matching
        whenever(scmCatalogProvider.matches(entry, project)).thenReturn(true)
        // Collection of links
        catalogLinkService.computeCatalogLinks()
        // Checks that link is stored
        verify(entityDataService).store(
                project,
                CatalogLinkService::class.java.name,
                entry.key
        )
    }

    @Test
    fun `No link because project does not match entry`() {
        // List of projects
        whenever(structureService.projectList).thenReturn(listOf(project))
        // Catalog entries
        val entry = entry()
        whenever(scmCatalog.catalogEntries).thenReturn(sequenceOf(entry))
        // Matching
        whenever(scmCatalogProvider.matches(entry, project)).thenReturn(false)
        // Collection of links
        catalogLinkService.computeCatalogLinks()
        // Checks that link is NOT stored
        verify(entityDataService, never()).store(
                eq(project),
                eq(CatalogLinkService::class.java.name),
                any<String>()
        )
    }

    @Test
    fun `No link because provider not found`() {
        // List of projects
        whenever(structureService.projectList).thenReturn(listOf(project))
        // Catalog entries
        val entry = entry("unknown-provider")
        whenever(scmCatalog.catalogEntries).thenReturn(sequenceOf(entry))
        // Matching
        whenever(scmCatalogProvider.matches(entry, project)).thenReturn(true)
        // Collection of links
        catalogLinkService.computeCatalogLinks()
        // Checks that link is NOT stored
        verify(entityDataService, never()).store(
                eq(project),
                eq(CatalogLinkService::class.java.name),
                any<String>()
        )
    }

    @Test
    fun `Cleanup of left over keys`() {
        // List of projects
        whenever(structureService.projectList).thenReturn(listOf(project))
        // Catalog entries
        val entry = entry()
        whenever(scmCatalog.catalogEntries).thenReturn(sequenceOf(entry))
        // Matching
        whenever(scmCatalogProvider.matches(entry, project)).thenReturn(false)
        // Existing key
        whenever(entityDataService.retrieve(project, CatalogLinkService::class.java.name)).thenReturn(entry.key)
        // Collection of links
        catalogLinkService.computeCatalogLinks()
        // Asserts the link is deleted
        verify(entityDataService).delete(project, CatalogLinkService::class.java.name)
    }

    @Test
    fun `Cleanup of obsolete keys`() {
        // List of projects
        whenever(structureService.projectList).thenReturn(listOf(project))
        // Catalog entries
        val entry = entry()
        whenever(scmCatalog.catalogEntries).thenReturn(sequenceOf(entry))
        // Matching
        whenever(scmCatalogProvider.matches(entry, project)).thenReturn(false)
        // Existing key
        whenever(entityDataService.retrieve(project, CatalogLinkService::class.java.name)).thenReturn("unknown-key")
        // Collection of links
        catalogLinkService.computeCatalogLinks()
        // Asserts the link is deleted
        verify(entityDataService).delete(project, CatalogLinkService::class.java.name)
    }

    @Test
    fun `Getting a catalog entry from a project`() {
        val entry = entry()
        whenever(entityDataService.retrieve(project, CatalogLinkService::class.java.name)).thenReturn(entry.key)
        whenever(scmCatalog.getCatalogEntry(entry.key)).thenReturn(entry)
        val loaded = catalogLinkService.getSCMCatalogEntry(project)
        assertEquals(entry, loaded)
    }

    @Test
    fun `Catalog entry not found for a project`() {
        val entry = entry()
        whenever(entityDataService.retrieve(project, CatalogLinkService::class.java.name)).thenReturn(entry.key)
        whenever(scmCatalog.getCatalogEntry(entry.key)).thenReturn(null)
        val loaded = catalogLinkService.getSCMCatalogEntry(project)
        assertNull(loaded)
    }

    @Test
    fun `Getting linked project from an entry`() {
        val entry = entry()
        whenever(entityDataService.findEntityByValue(
                ProjectEntityType.PROJECT,
                CatalogLinkService::class.java.name,
                entry.key.asJson()
        )).thenReturn(ProjectEntityID(ProjectEntityType.PROJECT, project.id))
        whenever(structureService.getProject(project.id)).thenReturn(project)
        val loaded = catalogLinkService.getLinkedProject(entry)
        assertEquals(project, loaded)
    }

    @Test
    fun `Getting linked flag from an entry`() {
        val entry = entry()
        whenever(entityDataService.findEntityByValue(
                ProjectEntityType.PROJECT,
                CatalogLinkService::class.java.name,
                entry.key.asJson()
        )).thenReturn(ProjectEntityID(ProjectEntityType.PROJECT, project.id))
        val linked = catalogLinkService.isLinked(entry)
        assertEquals(true, linked)
    }

    @Test
    fun `No linked project from an entry`() {
        val entry = entry()
        whenever(structureService.projectList).thenReturn(listOf(project))
        whenever(entityDataService.retrieve(project, CatalogLinkService::class.java.name)).thenReturn("unknown-key")
        val loaded = catalogLinkService.getLinkedProject(entry)
        assertNull(loaded)
    }

}