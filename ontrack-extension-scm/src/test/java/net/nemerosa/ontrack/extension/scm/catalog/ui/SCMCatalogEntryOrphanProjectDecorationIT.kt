package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SCMCatalogEntryOrphanProjectDecorationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var scmCatalog: SCMCatalog

    @Autowired
    private lateinit var catalogLinkService: CatalogLinkService

    @Autowired
    private lateinit var decoration: SCMCatalogEntryOrphanProjectDecoration

    @Test
    fun `Project without an associated SCM catalog entry is marked as orphan`() {
        project {
            val decorations = decoration.getDecorations(this)
            assertEquals(1, decorations.size)
        }
    }

    @Test
    fun `Project with an associated SCM catalog entry is not marked as orphan`() {
        // Collection of entries
        scmCatalog.collectSCMCatalog { println(it) }
        // Link with project
        val entry = CatalogFixtures.entry()
        val name = TestUtils.uid("repository-")
        project(nd(name, "")) {
            // Collection of catalog links
            catalogLinkService.computeCatalogLinks()
            // Gets the decoration
            val decorations = decoration.getDecorations(this)
            assertEquals(0, decorations.size)
        }
    }

}