package net.nemerosa.ontrack.extension.scm.catalog.mock

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class MockSCMCatalogProvider : SCMCatalogProvider {

    override val id: String = "mocking"

    override val entries: List<SCMCatalogSource> = listOf(
            SCMCatalogSource(
                    "MainConfig",
                    "project/repository",
                    "uri:project/repository"
            )
    )

    override fun matches(entry: SCMCatalogEntry, project: Project): Boolean =
            project.name.startsWith("repository-")
}