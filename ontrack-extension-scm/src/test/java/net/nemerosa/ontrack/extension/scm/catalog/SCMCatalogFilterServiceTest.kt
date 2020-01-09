package net.nemerosa.ontrack.extension.scm.catalog

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures.entry
import net.nemerosa.ontrack.model.security.SecurityService
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SCMCatalogFilterServiceTest {

    private lateinit var catalog: SCMCatalog
    private lateinit var catalogLinkService: CatalogLinkService
    private lateinit var securityService: SecurityService

    private lateinit var service: SCMCatalogFilterService

    @Before
    fun init() {
        catalog = mock()
        catalogLinkService = mock()
        securityService = mock()
        service = SCMCatalogFilterServiceImpl(
                catalog,
                catalogLinkService,
                securityService
        )
    }

    @Test
    fun `Find all entries`() {
        doTest {
            +"repo-1"
            +"repo-2-linked"
            +"repo-3-other-config"
            +"repo-4-other-config-linked"
            +"repo-5-other-scm"
            +"repo-6-other-scm-linked"
        }
    }

    @Test
    fun `SCM filter`() {
        doTest(scm = "scm-1") {
            +"repo-1"
            +"repo-2-linked"
            +"repo-3-other-config"
            +"repo-4-other-config-linked"
        }
    }

    @Test
    fun `SCM and config filter`() {
        doTest(scm = "scm-1", config = "config-1") {
            +"repo-1"
            +"repo-2-linked"
        }
    }

    @Test
    fun `Config filter`() {
        doTest(config = "config-2") {
            +"repo-3-other-config"
            +"repo-4-other-config-linked"
        }
    }

    @Test
    fun `Repository filter`() {
        doTest(repository = ".*other.*") {
            +"repo-3-other-config"
            +"repo-4-other-config-linked"
            +"repo-5-other-scm"
            +"repo-6-other-scm-linked"
        }
    }

    @Test
    fun `Repository and SCM filter`() {
        doTest(repository = ".*other.*", scm = "scm-1") {
            +"repo-3-other-config"
            +"repo-4-other-config-linked"
        }
    }

    @Test
    fun `Linked entries`() {
        doTest(link = SCMCatalogFilterLink.LINKED) {
            +"repo-2-linked"
            +"repo-4-other-config-linked"
            +"repo-6-other-scm-linked"
        }
    }

    @Test
    fun `Linked entries filtered by SCM`() {
        doTest(link = SCMCatalogFilterLink.LINKED, scm = "scm-1") {
            +"repo-2-linked"
            +"repo-4-other-config-linked"
        }
    }

    @Test
    fun `Linked entries filtered by SCM and config`() {
        doTest(link = SCMCatalogFilterLink.LINKED, scm = "scm-1", config = "config-1") {
            +"repo-2-linked"
        }
    }

    @Test
    fun `Orphan entries`() {
        doTest(link = SCMCatalogFilterLink.ORPHAN) {
            +"repo-1"
            +"repo-3-other-config"
            +"repo-5-other-scm"
        }
    }

    private fun doTest(
            offset: Int = 0,
            size: Int = 20,
            scm: String? = null,
            config: String? = null,
            repository: String? = null,
            link: SCMCatalogFilterLink = SCMCatalogFilterLink.ALL,
            expectations: Expectations.() -> Unit) {
        val store = listOf(
                entry(scm = "scm-1", config = "config-1", repository = "repo-1"),
                entry(scm = "scm-1", config = "config-1", repository = "repo-2-linked"),
                entry(scm = "scm-1", config = "config-2", repository = "repo-3-other-config"),
                entry(scm = "scm-1", config = "config-2", repository = "repo-4-other-config-linked"),
                entry(scm = "scm-2", config = "config-3", repository = "repo-5-other-scm"),
                entry(scm = "scm-2", config = "config-3", repository = "repo-6-other-scm-linked")
        )
        whenever(catalog.catalogEntries).thenReturn(store.asSequence())

        store.forEach { entry ->
            whenever(catalogLinkService.isLinked(entry)).thenReturn(
                    entry.repository.endsWith("-linked")
            )
        }

        val expectationsContext = Expectations()
        expectationsContext.expectations()

        val entries = service.findCatalogEntries(SCMCatalogFilter(
                offset, size, scm, config, repository, link
        ))
        assertEquals(
                expectationsContext.repos,
                entries.map { it.repository }
        )
    }

    @DslMarker
    internal annotation class ExpectationsDsl

    @ExpectationsDsl
    internal class Expectations {

        val repos = mutableListOf<String>()

        operator fun String.unaryPlus() {
            repos += this
        }

    }

}