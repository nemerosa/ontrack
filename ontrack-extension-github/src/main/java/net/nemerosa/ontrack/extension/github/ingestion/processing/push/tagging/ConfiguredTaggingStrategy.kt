package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build

class ConfiguredTaggingStrategy<C>(
    val strategy: TaggingStrategy<C>,
    val config: C?,
) {
    fun findBuild(branch: Branch, payload: PushPayload): Build? =
        strategy.findBuild(config, branch, payload)
}
