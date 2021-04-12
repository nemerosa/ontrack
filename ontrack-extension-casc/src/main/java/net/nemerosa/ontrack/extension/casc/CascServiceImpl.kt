package net.nemerosa.ontrack.extension.casc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import net.nemerosa.ontrack.extension.casc.context.OntrackContext
import net.nemerosa.ontrack.json.merge
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CascServiceImpl(
    private val ontrackContext: OntrackContext,
    private val securityService: SecurityService,
) : net.nemerosa.ontrack.extension.casc.CascService {

    private val mapper = ObjectMapper(YAMLFactory())

    override fun runYaml(yaml: List<String>) {
        // Parsing each YAML content
        val parsed = yaml.map {
            it.parse()
        }
        // Running
        run(parsed)
    }

    private fun run(nodes: List<JsonNode>) {
        val merged = nodes.fold(NullNode.instance as JsonNode) { acc, r ->
            acc.merge(r)
        }
        run(merged)
    }

    private fun run(node: JsonNode) {
        // We want the root to be `ontrack`
        val ontrack = node.path(ROOT)
        if (ontrack.isNull || !ontrack.isObject) {
            error("Root of the Ontrack CasC must be `$ROOT`.")
        } else {
            // Starts running the CasC
            securityService.asAdmin {
                ontrackContext.run(ontrack as ObjectNode, listOf(ROOT))
            }
        }
    }

    private fun String.parse(): JsonNode =
        mapper.readTree(this)

    companion object {
        const val ROOT = "ontrack"
    }

}