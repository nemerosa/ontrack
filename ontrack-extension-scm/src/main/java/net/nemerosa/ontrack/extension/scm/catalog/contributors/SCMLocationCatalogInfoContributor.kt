package net.nemerosa.ontrack.extension.scm.catalog.contributors

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.service.SCMServiceDetector
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class SCMLocationCatalogInfoContributor(
        extensionFeature: SCMExtensionFeature,
        private val structureService: StructureService,
        private val scmServiceDetector: SCMServiceDetector
) : AbstractCoreCatalogInfoContributor<SCMLocationCatalogInfoContributor.SCMLocation>(extensionFeature) {

    override fun collectInfo(project: Project, entry: SCMCatalogEntry): SCMLocation? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun asJson(info: SCMLocation): JsonNode = info.asJson()

    override fun fromJson(node: JsonNode): SCMLocation = node.parse()

    data class SCMLocation(
            val scm: String,
            val name: String,
            val uri: String,
            val url: String
    )

}