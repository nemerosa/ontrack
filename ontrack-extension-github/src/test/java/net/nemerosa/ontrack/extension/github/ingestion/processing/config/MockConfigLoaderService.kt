package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.model.structure.Branch

class MockConfigLoaderService : ConfigLoaderService {

    override fun loadConfig(branch: Branch, path: String): IngestionConfig? {
        TODO("Not yet implemented")
    }
}