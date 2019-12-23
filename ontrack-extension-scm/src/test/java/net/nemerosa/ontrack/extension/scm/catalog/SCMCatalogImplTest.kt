package net.nemerosa.ontrack.extension.scm.catalog

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.support.StorageService
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SCMCatalogImplTest {

    private lateinit var storageService: StorageService

    @Before
    fun init() {
        storageService = mock()
    }

    @Test
    fun `Registering entries without any provider`() {
        val catalog = SCMCatalogImpl(storageService, emptyList())
        whenever(storageService.getKeys("scm-catalog")).thenReturn(emptyList())
        catalog.collectSCMCatalog { println(it) }
        verify(storageService, times(0)).store(eq("scm-catalog"), any(), any())
        verify(storageService, times(0)).delete(eq("scm-catalog"), any())
    }

    @Test
    fun `Registering entries with one provider`() {
        val provider: SCMCatalogProvider = mock()
        whenever(provider.id).thenReturn("scm")
        whenever(provider.entries).thenReturn(listOf(source("project/repo1")))

        val catalog = SCMCatalogImpl(storageService, listOf(provider))
        whenever(storageService.getKeys("scm-catalog")).thenReturn(emptyList())
        catalog.collectSCMCatalog { println(it) }
        verify(storageService, times(1)).store(
                eq("scm-catalog"),
                eq("scm::config::project/repo1"),
                argThat<SCMCatalogEntry> {
                    scm == "scm" && config == "config" && repository == "project/repo1"
                }
        )
        verify(storageService, times(0)).delete(eq("scm-catalog"), any())
    }

    @Test
    fun `Registering entries with two providers`() {
        val provider1: SCMCatalogProvider = mock()
        whenever(provider1.id).thenReturn("scm")
        whenever(provider1.entries).thenReturn(listOf(source("project/repo1")))

        val provider2: SCMCatalogProvider = mock()
        whenever(provider2.id).thenReturn("scm2")
        whenever(provider2.entries).thenReturn(listOf(source("project/repo2")))

        val catalog = SCMCatalogImpl(storageService, listOf(provider1, provider2))
        whenever(storageService.getKeys("scm-catalog")).thenReturn(emptyList())
        catalog.collectSCMCatalog { println(it) }
        verify(storageService, times(1)).store(
                eq("scm-catalog"),
                eq("scm::config::project/repo1"),
                argThat<SCMCatalogEntry> {
                    scm == "scm" && config == "config" && repository == "project/repo1"
                }
        )
        verify(storageService, times(1)).store(
                eq("scm-catalog"),
                eq("scm2::config::project/repo2"),
                argThat<SCMCatalogEntry> {
                    scm == "scm2" && config == "config" && repository == "project/repo2"
                }
        )
        verify(storageService, times(0)).delete(eq("scm-catalog"), any())
    }

    @Test
    fun `Registering entries with one addition and one update`() {
        val provider: SCMCatalogProvider = mock()
        whenever(provider.id).thenReturn("scm")
        whenever(provider.entries).thenReturn(listOf(source("project/repo1"), source("project/repo2")))

        val catalog = SCMCatalogImpl(storageService, listOf(provider))
        whenever(storageService.getKeys("scm-catalog")).thenReturn(listOf("scm::config::project/repo1"))
        catalog.collectSCMCatalog { println(it) }
        verify(storageService, times(1)).store(
                eq("scm-catalog"),
                eq("scm::config::project/repo1"),
                argThat<SCMCatalogEntry> {
                    scm == "scm" && config == "config" && repository == "project/repo1"
                }
        )
        verify(storageService, times(1)).store(
                eq("scm-catalog"),
                eq("scm::config::project/repo2"),
                argThat<SCMCatalogEntry> {
                    scm == "scm" && config == "config" && repository == "project/repo2"
                }
        )
        verify(storageService, times(0)).delete(eq("scm-catalog"), any())
    }

    @Test
    fun `Registering entries with one addition and one deletion`() {
        val provider: SCMCatalogProvider = mock()
        whenever(provider.id).thenReturn("scm")
        whenever(provider.entries).thenReturn(listOf(source("project/repo1")))

        val catalog = SCMCatalogImpl(storageService, listOf(provider))
        whenever(storageService.getKeys("scm-catalog")).thenReturn(listOf("scm::config::project/repo2"))
        catalog.collectSCMCatalog { println(it) }
        verify(storageService, times(1)).store(
                eq("scm-catalog"),
                eq("scm::config::project/repo1"),
                argThat<SCMCatalogEntry> {
                    scm == "scm" && config == "config" && repository == "project/repo1"
                }
        )
        verify(storageService).delete("scm-catalog", "scm::config::project/repo2")
    }

    @Test
    fun `Getting entries`() {
        val catalog = SCMCatalogImpl(storageService, emptyList())
        whenever(storageService.getData("scm-catalog", SCMCatalogEntry::class.java)).thenReturn(
                mapOf(
                        "key1" to entry("project/repo1"),
                        "key2" to entry("project/repo2")
                )
        )
        val repositories = catalog.catalogEntries.map { it.repository }.sorted().toList()
        assertEquals(
                listOf("project/repo1", "project/repo2"),
                repositories
        )
    }

    @Test
    fun `Getting an entry by key`() {
        val catalog = SCMCatalogImpl(storageService, emptyList())
        whenever(storageService.retrieve("scm-catalog", "key1", SCMCatalogEntry::class.java)).thenReturn(
                Optional.of(entry("project/repo"))
        )
        assertNotNull(catalog.getCatalogEntry("key1")) {
            assertEquals("project/repo", it.repository)
        }
        assertNull(catalog.getCatalogEntry("key2"))
    }

    private fun entry(name: String, scm: String = "scm") = SCMCatalogEntry(
            scm,
            "config",
            name,
            "https://scm/$name",
            Time.now()
    )

    private fun source(name: String) = SCMCatalogSource(
            "config",
            name,
            "https://scm/$name"
    )


}