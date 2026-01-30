package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.storage.SlotPipelineRepository
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingService
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingServiceConfig
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import net.nemerosa.ontrack.model.templating.getEnum
import org.springframework.stereotype.Component

@Component
@Documentation(ChangelogDeploymentTemplatingContextConfig::class)
@APIDescription("Getting the changelog since a previous deployment")
class ChangelogDeploymentTemplatingContextFieldHandler(
    private val slotPipelineRepository: SlotPipelineRepository,
    private val changeLogTemplatingService: ChangeLogTemplatingService,
) : DeploymentTemplatingContextFieldHandler {

    override val field: String = "changelog"

    override fun render(deployment: SlotPipeline, config: TemplatingSourceConfig, renderer: EventRenderer): String {
        val empty = ChangeLogTemplatingServiceConfig.emptyValue(config)

        val since =
            config.getEnum<SlotPipelineStatus>(ChangelogDeploymentTemplatingContextConfig::since.name)
                ?: SlotPipelineStatus.DONE

        val toBuild = deployment.build

        val sinceDeployment = slotPipelineRepository.findLastPipelineBySlotAndStatusExcludingOne(
            slot = deployment.slot,
            status = since,
            excludedPipeline = deployment,
        ) ?: return empty

        return changeLogTemplatingService.render(
            fromBuild = sinceDeployment.build,
            toBuild = toBuild,
            config = config.parse(),
            renderer = renderer,
        )
    }
}
