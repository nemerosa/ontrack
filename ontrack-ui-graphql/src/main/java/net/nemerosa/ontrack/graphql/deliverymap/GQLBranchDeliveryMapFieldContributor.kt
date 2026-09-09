package net.nemerosa.ontrack.graphql.deliverymap

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.deliverymap.DeliveryMap
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

/**
 * Exposes the delivery map of a branch, as `branch.deliveryMap`.
 */
@Component
class GQLBranchDeliveryMapFieldContributor(
    private val deliveryMapService: DeliveryMapService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition> =
        if (projectEntityType == ProjectEntityType.BRANCH) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("deliveryMap")
                    .description(
                        "What a build on this branch has to pass through on its way to an environment"
                    )
                    .type(GraphQLNonNull(GraphQLTypeReference(DeliveryMap::class.java.simpleName)))
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()!!
                        deliveryMapService.getDeliveryMap(branch)
                    }
                    .build()
            )
        } else {
            emptyList()
        }
}
