package net.nemerosa.ontrack.extension.scm.catalog

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures.entry
import net.nemerosa.ontrack.extension.scm.catalog.mock.MockCatalogInfoContributor
import net.nemerosa.ontrack.extension.scm.catalog.mock.MockInfo
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecord
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CatalogInfoCollectorImplTest {

    private lateinit var catalogLinkService: CatalogLinkService
    private lateinit var extensionManager: ExtensionManager
    private lateinit var entityDataStore: EntityDataStore
    private lateinit var securityService: SecurityService
    private lateinit var applicationLogService: ApplicationLogService

    private lateinit var collector: CatalogInfoCollector

    private lateinit var project: Project

    @Before
    fun setup() {
        project = Project.of(NameDescription.nd("PRJ", "")).withId(ID.of(1))

        catalogLinkService = mock()
        extensionManager = mock()
        entityDataStore = mock()
        securityService = mock()
        applicationLogService = mock()

        collector = CatalogInfoCollectorImpl(catalogLinkService, extensionManager, entityDataStore, securityService, applicationLogService)
    }

    @Test
    fun `Deleting all info if no catalog entry for project`() {
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(null)
        collector.collectCatalogInfo(project) { println(it) }
        verify(entityDataStore).deleteByFilter(EntityDataStoreFilter(
                entity = project,
                category = STORE
        ))
    }

    @Test
    fun `Deleting info if no info for project`() {
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(entry("unknown-scm"))
        registerMockContributor()
        collector.collectCatalogInfo(project) { println(it) }
        verify(entityDataStore).deleteByFilter(EntityDataStoreFilter(
                entity = project,
                category = STORE,
                name = MockCatalogInfoContributor::class.java.name
        ))
    }

    @Test
    fun `Storing catalog info`() {
        val signature = Signature.of("test")
        whenever(securityService.currentSignature).thenReturn(signature)
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(entry("scm"))
        registerMockContributor()
        collector.collectCatalogInfo(project) { println(it) }
        verify(entityDataStore).add(
                eq(project),
                eq(STORE),
                eq(MockCatalogInfoContributor::class.java.name),
                eq(signature),
                eq(null),
                eq(MockInfo("project/repository").asJson())
        )
    }

    @Test
    fun `Logging an error if error while collecting catalog info for project`() {
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(entry("error"))
        registerMockContributor()
        collector.collectCatalogInfo(project) { println(it) }
        verifyZeroInteractions(entityDataStore)
        verify(applicationLogService).log(any())
    }

    @Test
    fun `Loading non existing catalog info`() {
        whenever(entityDataStore.getByFilter(EntityDataStoreFilter(
                entity = project,
                category = STORE
        ))).thenReturn(emptyList())
        val infos = collector.getCatalogInfos(project)
        assertEquals(0, infos.size)
    }

    @Test
    fun `Loading catalog info for a non registered contributor`() {
        registerMockContributor()
        whenever(entityDataStore.getByFilter(EntityDataStoreFilter(
                entity = project,
                category = STORE
        ))).thenReturn(listOf(
                EntityDataStoreRecord(
                        0,
                        project,
                        STORE,
                        "unknown-contributor",
                        null,
                        Signature.of("test"),
                        MockInfo("test").asJson()
                )
        ))
        val infos = collector.getCatalogInfos(project)
        assertEquals(0, infos.size)
    }

    @Test
    fun `Loading catalog info`() {
        registerMockContributor()
        val signature = Signature.of("test")
        whenever(entityDataStore.getByFilter(EntityDataStoreFilter(
                entity = project,
                category = STORE
        ))).thenReturn(listOf(
                EntityDataStoreRecord(
                        0,
                        project,
                        STORE,
                        MockCatalogInfoContributor::class.java.name,
                        null,
                        signature,
                        MockInfo("test").asJson()
                )
        ))
        val infos = collector.getCatalogInfos(project)
        assertEquals(1, infos.size)
        @Suppress("UNCHECKED_CAST")
        val info: CatalogInfo<MockInfo> = infos.first() as CatalogInfo<MockInfo>
        assertEquals("mock", info.collector.name)
        assertEquals(signature.time, info.timestamp)
        assertEquals(MockInfo("test"), info.data)
    }

    private fun registerMockContributor() {
        whenever(extensionManager.getExtensions(CatalogInfoContributor::class.java)).thenReturn(listOf(MockCatalogInfoContributor()))
    }

}

private const val STORE = "net.nemerosa.ontrack.extension.scm.catalog.CatalogInfoCollector"
