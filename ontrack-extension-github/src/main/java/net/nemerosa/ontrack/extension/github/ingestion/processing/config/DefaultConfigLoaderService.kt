package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.FileLoaderService
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.ConfigParser
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

@Component
class DefaultConfigLoaderService(
    private val fileLoaderService: FileLoaderService,
) : ConfigLoaderService {

    override fun loadConfig(
        configuration: GitHubEngineConfiguration,
        repository: String,
        branch: String,
        path: String,
    ): IngestionConfig? =
        fileLoaderService.loadFile(configuration, repository, branch, path)?.let { content ->
            ConfigParser.parseYaml(content)
        }

    override fun loadConfig(branch: Branch, path: String): IngestionConfig? {
        val content = fileLoaderService.loadFile(branch, path) ?: return null
        return ConfigParser.parseYaml(content)
    }
}