package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class DefaultConfigLoaderService(
    private val gitHubClientFactory: OntrackGitHubClientFactory,
    private val propertyService: PropertyService,
) : ConfigLoaderService {

    private val yamlFactory = YAMLFactory().apply {
        enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
    }

    private val mapper = ObjectMapper(yamlFactory)

    override fun loadConfig(branch: Branch, path: String): IngestionConfig? {
        val gitHubProjectProperty =
            propertyService.getProperty(branch.project, GitHubProjectConfigurationPropertyType::class.java).value
                ?: return null
        val gitBranchProperty =
            propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java).value
                ?: return null
        val client = gitHubClientFactory.create(gitHubProjectProperty.configuration)
        val binaryContent =
            client.getFileContent(gitHubProjectProperty.repository, gitBranchProperty.branch, path) ?: return null
        // Assuming UTF-8
        val content = binaryContent.toString(Charsets.UTF_8)
        // Parsing as YAML
        return try {
            mapper.readTree(content).parse()
        } catch (_: Exception) {
            null // Ignoring any parsing exception
        }
    }
}