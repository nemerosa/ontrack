package net.nemerosa.ontrack.extension.github.ingestion.processing.buildid

abstract class AbstractBuildIdStrategy(
    override val id: String,
) : BuildIdStrategy
