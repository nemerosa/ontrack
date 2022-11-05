package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.ConfigParser
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component

/**
 * Displays the ingestion configuration, if any, for a branch.
 */
@Component
class BranchGitHubIngestionConfigInformationExtension(
    extensionFeature: GitHubExtensionFeature,
    private val configService: ConfigService,
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override fun getInformation(entity: ProjectEntity): EntityInformation? =
        if (entity is Branch) {
            configService.findConfig(entity)?.let { config ->
                EntityInformation(
                    this,
                    mapOf(
                        "yaml" to ConfigParser.toYaml(config)
                    )
                )
            }
        } else {
            null
        }


}