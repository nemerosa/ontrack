package net.nemerosa.ontrack.extension.github.ingestion.processing.buildid

import org.springframework.stereotype.Component

@Component
class BuildIdStrategyRegistryImpl(
    buildIdStrategies: List<BuildIdStrategy>,
) : BuildIdStrategyRegistry {

    private val index = buildIdStrategies.associateBy { it.id }

    override fun getBuildIdStrategy(id: String?): BuildIdStrategy {
        val actualId = id ?: CommitBuildIdStrategy.ID
        return index[actualId] ?: throw BuildIdStrategyNotFoundException(actualId)
    }
}