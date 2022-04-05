package net.nemerosa.ontrack.extension.casc.context.core.admin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class MockAdminContext : AbstractCascContext(), SubAdminContext {

    var data: MockData? = null

    override val field: String = "mock"

    override val type: CascType = cascObject(MockData::class)

    override fun run(node: JsonNode, paths: List<String>) {
        data = node.parse()
    }

    override fun render(): JsonNode =
        data?.asJson() ?: NullNode.instance

    data class MockData(
        val username: String,
        val password: String,
    )
}