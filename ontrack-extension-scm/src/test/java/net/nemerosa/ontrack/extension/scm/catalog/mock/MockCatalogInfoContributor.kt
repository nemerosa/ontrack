package net.nemerosa.ontrack.extension.scm.catalog.mock

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.AbstractCatalogInfoContributor
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class MockCatalogInfoContributor : AbstractCatalogInfoContributor<MockInfo>(TestExtensionFeature()) {

    override fun collectInfo(project: Project, entry: SCMCatalogEntry): MockInfo? =
            when (entry.scm) {
                "scm" -> MockInfo(entry.repository)
                "mocking" -> MockInfo("${project.name}@${entry.repository}")
                "error" -> throw IllegalStateException("Error while collecting information")
                else -> null
            }

    override fun asStoredJson(info: MockInfo): JsonNode = info.asJson()

    override fun fromStoredJson(project: Project, node: JsonNode): MockInfo? = node.parse()

    override fun asClientJson(info: MockInfo): JsonNode = info.asJson()

    override val name: String = "mock"

}
