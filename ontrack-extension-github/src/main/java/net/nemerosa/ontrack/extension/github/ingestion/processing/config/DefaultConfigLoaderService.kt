package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.FileLoaderService
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

@Component
class DefaultConfigLoaderService(
    private val fileLoaderService: FileLoaderService,
) : ConfigLoaderService {

    override fun loadConfig(branch: Branch, path: String): IngestionConfig? {
        val content = fileLoaderService.loadFile(branch, path) ?: return null
        return ConfigParser.parseYaml(content)
    }
}