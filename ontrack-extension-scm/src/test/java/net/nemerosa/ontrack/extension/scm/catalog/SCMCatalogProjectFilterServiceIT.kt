package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.extension.scm.catalog.mock.MockSCMCatalogProvider
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SCMCatalogProjectFilterServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var scmCatalog: SCMCatalog

    @Autowired
    private lateinit var catalogLinkService: CatalogLinkService

    @Autowired
    private lateinit var scmCatalogFilterService: SCMCatalogFilterService

    @Autowired
    private lateinit var scmCatalogProvider: MockSCMCatalogProvider

    private lateinit var projectLinked: Project
    private lateinit var projectLinkedOther: Project
    private lateinit var projectOrphan: Project

    @Before
    fun projects() {
        projectLinked = project {}
        projectLinkedOther = project {}
        projectOrphan = project {}
    }

    @Test
    fun `All entries`() {
        doTest {
            expect(REPO_LINKED)
            expect(REPO_LINKED_OTHER)
            expect(REPO_UNLINKED)
            orphan(projectOrphan)
        }
    }

    @Test
    fun `All entries with explicit filter`() {
        doTest(link = SCMCatalogProjectFilterLink.ALL) {
            expect(REPO_LINKED)
            expect(REPO_LINKED_OTHER)
            expect(REPO_UNLINKED)
            orphan(projectOrphan)
        }
    }

    @Test
    fun `Filter on SCM`() {
        doTest(scm = "test") {
            // expect(REPO_LINKED)
            // expect(REPO_LINKED_OTHER)
            // expect(REPO_UNLINKED)
            orphan(projectOrphan)
        }
    }

    @Test
    fun `Filter on configuration`() {
        doTest(config = "config-1") {
            expect(REPO_LINKED)
            // expect(REPO_LINKED_OTHER)
            // expect(REPO_UNLINKED)
            orphan(projectOrphan)
        }
    }

    @Test
    fun `Filter on repository`() {
        doTest(repository = ".*repo.*") {
            expect(REPO_LINKED)
            expect(REPO_LINKED_OTHER)
            expect(REPO_UNLINKED)
            orphan(projectOrphan)
        }
    }

    @Test
    fun `Filter on specific repository`() {
        doTest(repository = REPO_LINKED_OTHER) {
            // expect(REPO_LINKED)
            expect(REPO_LINKED_OTHER)
            // expect(REPO_UNLINKED)
            orphan(projectOrphan)
        }
    }

    @Test
    fun `Linked entries only`() {
        doTest(link = SCMCatalogProjectFilterLink.LINKED) {
            expect(REPO_LINKED)
            expect(REPO_LINKED_OTHER)
        }
    }

    @Test
    fun `Unlinked entries only`() {
        doTest(link = SCMCatalogProjectFilterLink.UNLINKED) {
            expect(REPO_UNLINKED)
        }
    }

    @Test
    fun `Orphan entries only`() {
        doTest(link = SCMCatalogProjectFilterLink.ORPHAN) {
            orphan(projectOrphan)
        }
    }

    @Test
    fun `Orphan entries only with positive regex`() {
        doTest(link = SCMCatalogProjectFilterLink.ORPHAN, project = ".*${projectOrphan.name}.*") {
            orphan(projectOrphan)
        }
    }

    @Test
    fun `Orphan entries only with negative regex`() {
        doTest(link = SCMCatalogProjectFilterLink.ORPHAN, project = ".*${projectOrphan.name}notamatch.*") {
            notOrphan(projectOrphan)
        }
    }

    private fun doTest(
            authorizedProjects: List<Project>? = null,
            offset: Int = 0,
            size: Int = 20,
            scm: String? = null,
            config: String? = null,
            repository: String? = null,
            project: String? = null,
            link: SCMCatalogProjectFilterLink = SCMCatalogProjectFilterLink.ALL,
            expectations: Expectations.() -> Unit
    ) {
        scmCatalogProvider.clear()

        // All entries
        val entryLinked = CatalogFixtures.entry(scm = "mocking", repository = REPO_LINKED, config = "config-1")
        val entryLinkedOther = CatalogFixtures.entry(scm = "mocking", repository = REPO_LINKED_OTHER, config = "config-2")
        val entryUnlinked = CatalogFixtures.entry(scm = "mocking", repository = REPO_UNLINKED, config = "config-3")

        // Mock data
        scmCatalogProvider.storeEntry(entryLinked)
        scmCatalogProvider.storeEntry(entryLinkedOther)
        scmCatalogProvider.storeEntry(entryUnlinked)
        // Mock links
        scmCatalogProvider.linkEntry(entryLinked, projectLinked)
        scmCatalogProvider.linkEntry(entryLinkedOther, projectLinkedOther)

        asAdmin {
            // Collection of entries
            scmCatalog.collectSCMCatalog { println(it) }
            // Collection of catalog links
            catalogLinkService.computeCatalogLinks()
        }

        // Testing context
        val expectationsContext = Expectations()
        expectationsContext.expectations()

        // Security context
        val securityContext: (() -> Unit) -> Unit = if (authorizedProjects != null) {
            {
                asUserWithView(*authorizedProjects.toTypedArray())
            }
        } else {
            {
                asAdmin(it)
            }
        }

        securityContext {
            val items = scmCatalogFilterService.findCatalogProjectEntries(
                    SCMCatalogProjectFilter(
                            offset = offset,
                            size = size,
                            scm = scm,
                            config = config,
                            repository = repository,
                            project = project,
                            link = link
                    )
            )
            // Repositories
            assertEquals(
                    expectationsContext.repos,
                    items.mapNotNull { it.entry?.repository }
            )
            // Orphan projects
            val actualOrphanProjectNames = items.filter { it.entry == null }.mapNotNull { it.project?.name }
            assertTrue(
                    // Actual list of orphan projects contains the expected one
                    // Note that the list of orphan projects will grow
                    // much larger than the expected ones
                    actual = actualOrphanProjectNames.containsAll(
                            expectationsContext.orphans.mapNotNull { it.project?.name }
                    ),
                    message = "Orphan projects"
            )
            // Not orphan projects
            assertTrue(
                    // Actual list of orphan projects DOES NOT contains any of the expected one
                    // Note that the list of orphan projects will grow
                    // much larger than the expected ones
                    actual = actualOrphanProjectNames.intersect(
                            expectationsContext.notOrphans.mapNotNull { it.project?.name }
                    ).isEmpty(),
                    message = "Not in orphan projects"
            )
        }
    }

    @DslMarker
    internal annotation class ExpectationsDsl

    @ExpectationsDsl
    internal class Expectations {

        val repos = mutableListOf<String>()
        val orphans = mutableListOf<Project>()
        val notOrphans = mutableListOf<Project>()

        fun expect(repo: String) {
            repos += repo
        }

        fun orphan(project: Project) {
            orphans += project
        }

        fun notOrphan(project: Project) {
            notOrphans += project
        }

    }

    companion object {
        private const val REPO_LINKED = "repo-1"
        private const val REPO_LINKED_OTHER = "repo-2"
        private const val REPO_UNLINKED = "repo-3"
    }

}