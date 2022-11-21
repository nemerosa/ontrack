package net.nemerosa.ontrack.extension.github.ingestion.processing.buildid

interface BuildIdStrategyRegistry {

    /**
     * Gets a build id strategy using its ID (or null to get the default build id strategy)
     */
    fun getBuildIdStrategy(id: String?): BuildIdStrategy
}