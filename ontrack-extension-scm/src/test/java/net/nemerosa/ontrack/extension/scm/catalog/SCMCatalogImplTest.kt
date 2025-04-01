package net.nemerosa.ontrack.extension.scm.catalog

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.StorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SCMCatalogImplTest {

    private lateinit var storageService: StorageService
    private lateinit var applicationLogService: ApplicationLogService

    @BeforeEach
    fun init() {
        storageService = mockk(relaxed = true)
        applicationLogService = mockk(relaxed = true)
    }

    @Test
    fun `Registering entries without any provider`() {
        val catalog = SCMCatalogImpl(storageService, emptyList(), applicationLogService)
        every { storageService.getKeys("scm-catalog") } returns emptyList()
        catalog.collectSCMCatalog { println(it) }
        verify(exactly = 0) { storageService.store(eq("scm-catalog"), any(), any()) }
        verify(exactly = 0) { storageService.delete(eq("scm-catalog"), any()) }
    }

    @Test
    fun `Registering entries with one provider`() {
        val provider: SCMCatalogProvider = mockk(relaxed = true)
        every { provider.id } returns "scm"
        every { provider.entries } returns listOf(source("project/repo1"))

        val catalog = SCMCatalogImpl(storageService, listOf(provider), applicationLogService)
        every { storageService.getKeys("scm-catalog") } returns emptyList()
        catalog.collectSCMCatalog { println(it) }
        verify(exactly = 1) {
            storageService.store(
                "scm-catalog",
                "scm::config::project/repo1",
                match<SCMCatalogEntry> {
                    it.scm == "scm" && it.config == "config" && it.repository == "project/repo1"
                }
            )
        }
        verify(exactly = 0) { storageService.delete(eq("scm-catalog"), any()) }
    }

    @Test
    fun `Registering entries with two providers`() {
        val provider1: SCMCatalogProvider = mockk(relaxed = true)
        every { provider1.id } returns "scm"
        every { provider1.entries } returns listOf(source("project/repo1"))

        val provider2: SCMCatalogProvider = mockk(relaxed = true)
        every { provider2.id } returns "scm2"
        every { provider2.entries } returns listOf(source("project/repo2"))

        val catalog = SCMCatalogImpl(storageService, listOf(provider1, provider2), applicationLogService)
        every { storageService.getKeys("scm-catalog") } returns emptyList()
        catalog.collectSCMCatalog { println(it) }
        verify(exactly = 1) {
            storageService.store(
                "scm-catalog",
                "scm::config::project/repo1",
                match<SCMCatalogEntry> {
                    it.scm == "scm" && it.config == "config" && it.repository == "project/repo1"
                }
            )
        }
        verify(exactly = 1) {
            storageService.store(
                "scm-catalog",
                "scm2::config::project/repo2",
                match<SCMCatalogEntry> {
                    it.scm == "scm2" && it.config == "config" && it.repository == "project/repo2"
                }
            )
        }
        verify(exactly = 0) { storageService.delete("scm-catalog", any()) }
    }

    @Test
    fun `Registering entries with one addition and one update`() {
        val provider: SCMCatalogProvider = mockk(relaxed = true)
        every { provider.id } returns "scm"
        every { provider.entries } returns listOf(source("project/repo1"), source("project/repo2"))

        val catalog = SCMCatalogImpl(storageService, listOf(provider), applicationLogService)
        every { storageService.getKeys("scm-catalog") } returns listOf("scm::config::project/repo1")
        catalog.collectSCMCatalog { println(it) }
        verify(exactly = 1) {
            storageService.store(
                "scm-catalog",
                "scm::config::project/repo1",
                match<SCMCatalogEntry> {
                    it.scm == "scm" && it.config == "config" && it.repository == "project/repo1"
                }
            )
        }
        verify(exactly = 1) {
            storageService.store(
                "scm-catalog",
                "scm::config::project/repo2",
                match<SCMCatalogEntry> {
                    it.scm == "scm" && it.config == "config" && it.repository == "project/repo2"
                }
            )
        }
        verify(exactly = 0) { storageService.delete(eq("scm-catalog"), any()) }
    }

    @Test
    fun `Registering entries with one addition and one deletion`() {
        val provider: SCMCatalogProvider = mockk(relaxed = true)
        every { provider.id } returns "scm"
        every { provider.entries } returns listOf(source("project/repo1"))

        val catalog = SCMCatalogImpl(storageService, listOf(provider), applicationLogService)
        every { storageService.getKeys("scm-catalog") } returns listOf("scm::config::project/repo2")
        catalog.collectSCMCatalog { println(it) }
        verify(exactly = 1) {
            storageService.store(
                "scm-catalog",
                "scm::config::project/repo1",
                match<SCMCatalogEntry> {
                    it.scm == "scm" && it.config == "config" && it.repository == "project/repo1"
                }
            )
        }
        verify {
            storageService.delete("scm-catalog", "scm::config::project/repo2")
        }
    }

    @Test
    fun `Getting entries`() {
        val catalog = SCMCatalogImpl(storageService, emptyList(), applicationLogService)
        every {
            storageService.getData("scm-catalog", SCMCatalogEntry::class.java)
        } returns mapOf(
            "key1" to entry("project/repo1"),
            "key2" to entry("project/repo2")
        )
        val repositories = catalog.catalogEntries.map { it.repository }.sorted().toList()
        assertEquals(
            listOf("project/repo1", "project/repo2"),
            repositories
        )
    }

    @Test
    fun `Getting an entry by key`() {
        val catalog = SCMCatalogImpl(storageService, emptyList(), applicationLogService)

        every {
            storageService.find("scm-catalog", "key1", SCMCatalogEntry::class)
        } returns entry("project/repo")

        every {
            storageService.find("scm-catalog", "key2", SCMCatalogEntry::class)
        } returns null

        assertNotNull(catalog.getCatalogEntry("key1")) {
            assertEquals("project/repo", it.repository)
        }

        assertNull(catalog.getCatalogEntry("key2"))
    }

    private fun entry(name: String, scm: String = "scm") = SCMCatalogEntry(
        scm = scm,
        config = "config",
        repository = name,
        repositoryPage = "https://scm/$name",
        lastActivity = Time.now(),
        createdAt = Time.now(),
        timestamp = Time.now(),
        teams = null
    )

    private fun source(name: String) = SCMCatalogSource(
        config = "config",
        repository = name,
        repositoryPage = "https://scm/$name",
        lastActivity = Time.now(),
        createdAt = Time.now()
    )

}