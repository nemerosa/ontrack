package net.nemerosa.ontrack.extension.github.resource

import net.nemerosa.ontrack.extension.github.GitHubController
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

@Component
class GitHubEngineConfigurationResourceDecorator :
    AbstractLinkResourceDecorator<GitHubEngineConfiguration>(GitHubEngineConfiguration::class.java) {

    /**
     * Obfuscates the password
     */
    override fun decorateBeforeSerialization(bean: GitHubEngineConfiguration): GitHubEngineConfiguration =
        bean.obfuscate()

    override fun getLinkDefinitions(): List<LinkDefinition<GitHubEngineConfiguration>> {
        return listOf(
            Link.SELF linkTo { configuration: GitHubEngineConfiguration ->
                MvcUriComponentsBuilder.on(GitHubController::class.java).getConfiguration(configuration.name)
            },
            Link.UPDATE linkTo { configuration: GitHubEngineConfiguration ->
                MvcUriComponentsBuilder.on(GitHubController::class.java).updateConfigurationForm(configuration.name)
            } linkIfGlobal GlobalSettings::class,
            Link.DELETE linkTo { configuration: GitHubEngineConfiguration ->
                MvcUriComponentsBuilder.on(GitHubController::class.java).deleteConfiguration(configuration.name)
            } linkIfGlobal GlobalSettings::class,
        )
    }
}