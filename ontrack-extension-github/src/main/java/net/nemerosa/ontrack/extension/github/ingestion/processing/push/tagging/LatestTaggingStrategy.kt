package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Tagging strategy based on getting the latest build on a branch.
 */
@Component
class LatestTaggingStrategy(
    private val structureService: StructureService,
) : TaggingStrategy<Any> {

    override val type: String = "latest"

    override fun parseAndValidate(config: JsonNode?): Any? = null

    override fun findBuild(config: Any?, branch: Branch, payload: PushPayload): Build? =
        structureService.getLastBuild(branch.id).getOrNull()

}