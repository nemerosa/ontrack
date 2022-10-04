package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.mock.MockSCMCatalogProvider
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractSCMCatalogSyncTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var scmCatalogProvider: MockSCMCatalogProvider

    @Autowired
    protected lateinit var scmCatalog: SCMCatalog

    @Autowired
    protected lateinit var catalogLinkService: CatalogLinkService

    protected lateinit var projectLinked: Project
    protected lateinit var projectLinkedOther: Project
    protected lateinit var projectOrphan: Project

    @BeforeEach
    fun projects() {
        projectLinked = project {
            structureService.saveProject(
                withDescription("Not to be overridden")
            )
        }
        projectLinkedOther = project {}
        projectOrphan = project {}
    }

    protected fun doTest(
        code: (
            entryLinked: SCMCatalogEntry,
            entryLinkedOther: SCMCatalogEntry,
            entryUnlinked: SCMCatalogEntry,
            entryUnlinkedOther: SCMCatalogEntry,
        ) -> Unit,
    ) {
        withSettings<SCMCatalogSyncSettings> {

            // Creating some entries
            scmCatalogProvider.clear()
            val entryLinked = CatalogFixtures.entry(scm = "mocking", repository = uid("linked"), config = "config-1")
            val entryLinkedOther =
                CatalogFixtures.entry(scm = "mocking", repository = uid("other"), config = "config-2")
            val entryUnlinked =
                CatalogFixtures.entry(scm = "mocking", repository = uid("unlinked"), config = "config-3")
            val entryUnlinkedOther =
                CatalogFixtures.entry(scm = "mocking", repository = uid("unlinked-other"), config = "config-3")

            // Mock data
            scmCatalogProvider.storeEntry(entryLinked)
            scmCatalogProvider.storeEntry(entryLinkedOther)
            scmCatalogProvider.storeEntry(entryUnlinked)
            scmCatalogProvider.storeEntry(entryUnlinkedOther)
            // Mock links
            scmCatalogProvider.linkEntry(entryLinked, projectLinked)
            scmCatalogProvider.linkEntry(entryLinkedOther, projectLinkedOther)

            asAdmin {
                // Collection of entries
                scmCatalog.collectSCMCatalog { println(it) }
                // Collection of catalog links
                catalogLinkService.computeCatalogLinks()
            }

            // Running the test
            code(entryLinked, entryLinkedOther, entryUnlinked, entryUnlinkedOther)
        }
    }

}