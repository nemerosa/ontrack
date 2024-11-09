package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.typedListField
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLBuildEligibleSlotsFieldContributor(
    private val environmentsLicense: EnvironmentsLicense,
    private val slotService: SlotService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BUILD && environmentsLicense.environmentFeatureEnabled) {
            listOf(
                typedListField<Build, Slot>(
                    type = Slot::class,
                    name = "eligibleSlots",
                    description = "List of slots this eligible to",
                ) { build ->
                    slotService.findEligibleSlotsByBuild(build)
                }
            )
        } else {
            null
        }

}