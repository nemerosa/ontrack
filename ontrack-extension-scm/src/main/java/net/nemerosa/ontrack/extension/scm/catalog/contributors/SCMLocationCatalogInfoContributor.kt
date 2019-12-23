package net.nemerosa.ontrack.extension.scm.catalog.contributors

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMLocation
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class SCMLocationCatalogInfoContributor(
        extensionFeature: SCMExtensionFeature,
        private val scmCatalogProviders: List<SCMCatalogProvider>
) : AbstractCoreCatalogInfoContributor<SCMLocation>(extensionFeature) {

    override val name: String = "SCM Location"

    override fun collectInfo(project: Project, entry: SCMCatalogEntry): SCMLocation? =
            scmCatalogProviders
                    .firstOrNull { it.matches(entry, project) }
                    ?.getSCMLocation(project)

    override fun asJson(info: SCMLocation): JsonNode = info.asJson()

    override fun fromJson(node: JsonNode): SCMLocation = node.parse()

}