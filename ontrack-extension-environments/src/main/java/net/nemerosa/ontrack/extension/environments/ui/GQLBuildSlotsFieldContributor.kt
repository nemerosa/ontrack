package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.graphql.support.toTypeRef
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLBuildSlotsFieldContributor(
    private val environmentsLicense: EnvironmentsLicense,
    private val slotService: SlotService,
) : GQLProjectEntityFieldContributor {

    companion object {
        private const val ARG_QUALIFIER = "qualifier"
        private const val ARG_ENVIRONMENT = "environment"
    }

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BUILD && environmentsLicense.environmentFeatureEnabled) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("slots")
                    .description("List of deployment slots this build is associated with")
                    .argument(stringArgument(ARG_QUALIFIER, "Qualifier for the project"))
                    .argument(stringArgument(ARG_ENVIRONMENT, "Name of the environment"))
                    .type(listType(Slot::class.toTypeRef()))
                    .dataFetcher { env ->
                        val build: Build = env.getSource()
                        val qualifier: String? = env.getArgument(ARG_QUALIFIER)
                        val environment: String? = env.getArgument(ARG_ENVIRONMENT)
                        slotService.findSlotsByProject(
                            project = build.project,
                            qualifier = qualifier,
                        )
                            .filter { environment.isNullOrBlank() || it.environment.name == environment }
                            .sortedBy { it.environment.order }
                    }
                    .build(),
            )
        } else {
            null
        }

}