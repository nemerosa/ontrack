package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.intArgument
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEligibleSlotsForBuild(
    private val gqlTypeEligibleSlot: GQLTypeEligibleSlot,
    private val structureService: StructureService,
    private val slotService: SlotService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("eligibleSlotsForBuild")
            .description("Getting the eligible slots for a build")
            .argument(intArgument(ARG_BUILD_ID, "ID of the build"))
            .type(listType(gqlTypeEligibleSlot.typeRef))
            .dataFetcher { env ->
                val buildId: Int = env.getArgument(ARG_BUILD_ID)
                val build = structureService.getBuild(ID.of(buildId))
                slotService.getEligibleSlotsForBuild(build)
            }
            .build()

    companion object {
        private const val ARG_BUILD_ID = "buildId"
    }
}