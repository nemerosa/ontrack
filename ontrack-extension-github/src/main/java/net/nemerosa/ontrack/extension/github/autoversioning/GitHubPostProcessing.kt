package net.nemerosa.ontrack.extension.github.autoversioning

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessing
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingMissingConfigException
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class GitHubPostProcessing(
    extensionFeature: GitHubExtensionFeature,
) : AbstractExtension(extensionFeature), PostProcessing<GitHubPostProcessingConfig> {

    override val id: String = "github"

    override val name: String = "GitHub Actions workflow post processing"

    override fun parseAndValidate(config: JsonNode?): GitHubPostProcessingConfig =
        if (config != null && !config.isNull) {
            // Parsing
            config.parse()
        } else {
            throw PostProcessingMissingConfigException()
        }

    override fun postProcessing(
        config: GitHubPostProcessingConfig,
        autoVersioningOrder: AutoVersioningOrder,
        repositoryUri: String,
        upgradeBranch: String,
    ) {
        TODO("Not yet implemented")
    }
}