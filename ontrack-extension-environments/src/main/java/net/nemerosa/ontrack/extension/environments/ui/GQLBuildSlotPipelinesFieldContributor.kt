package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLBuildSlotPipelinesFieldContributor(
    private val environmentsLicense: EnvironmentsLicense,
    private val slotService: SlotService,
) : GQLProjectEntityFieldContributor {

    companion object {
        private const val ARG_STATUS = "status"
        private const val ARG_SORTED_BY_ENVIRONMENT = "sortedByEnvironment"
        private const val ARG_QUALIFIER = "qualifier"
    }

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BUILD && environmentsLicense.environmentFeatureEnabled) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("slotPipelines")
                    .description("List of pipelines this build is associated with")
                    .argument(enumArgument<SlotPipelineStatus>(ARG_STATUS, "Filtering on the pipeline status"))
                    .argument(booleanArgument(ARG_SORTED_BY_ENVIRONMENT, "Sorting by decreasing environment order"))
                    .type(listType(SlotPipeline::class.toTypeRef()))
                    .dataFetcher { env ->
                        val build: Build = env.getSource()!!
                        val status = env.getArgument<String>(ARG_STATUS)?.let {
                            SlotPipelineStatus.valueOf(it)
                        }
                        val sortedByEnvironment: Boolean = env.getArgument(ARG_SORTED_BY_ENVIRONMENT) ?: false
                        val pipelines = slotService.findPipelineByBuild(build)
                            .filter { pipeline -> status == null || pipeline.status == status }
                        if (sortedByEnvironment) {
                            pipelines.sortedByDescending { it.slot.environment.order }
                        } else {
                            pipelines
                        }
                    }
                    .build(),
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("currentDeployments")
                    .description("Gets the list of slots where this build is actually deployed")
                    .argument(stringArgument(ARG_QUALIFIER, "Qualifier to use", defaultValue = Slot.DEFAULT_QUALIFIER))
                    .type(listType(SlotPipeline::class.toTypeRef()))
                    .dataFetcher { env ->
                        val build: Build = env.getSource()!!
                        val qualifier = env.getArgument<String>(ARG_QUALIFIER) ?: Slot.DEFAULT_QUALIFIER
                        slotService.findCurrentDeployments(build, qualifier)
                    }
                    .build(),
            )
        } else {
            null
        }

}