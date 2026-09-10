package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.workflows.notifications.EntityWorkflowInstanceService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import org.springframework.stereotype.Component

/**
 * Contributes a `workflowInstances` field to all project entities, returning the workflows which
 * have been launched by a notification on an event targeting this entity.
 *
 * How that link is made, and what it costs, belongs to [EntityWorkflowInstanceService]: the delivery
 * map reads the same thing for a whole branch at once (#1711), and the two must not answer it
 * differently.
 */
@Component
class GQLProjectEntityWorkflowInstancesFieldContributor(
    private val gqlTypeWorkflowInstance: GQLTypeWorkflowInstance,
    private val entityWorkflowInstanceService: EntityWorkflowInstanceService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition> = listOf(
        GraphQLFieldDefinition.newFieldDefinition()
            .name("workflowInstances")
            .description(
                "Workflows which have been launched by a notification on an event for this entity, " +
                        "most recent first. Resolved by scanning the " +
                        "${EntityWorkflowInstanceService.MAX_RECORDS} most recent workflow " +
                        "notification records for this entity, so the list may be truncated on entities " +
                        "which accumulate many of them, like a project or a branch."
            )
            .type(listType(gqlTypeWorkflowInstance.typeRef))
            .dataFetcher { env ->
                val entity: ProjectEntity = env.getSource()!!
                entityWorkflowInstanceService.findWorkflowInstancesByEntity(entity.toProjectEntityID())
            }
            .build()
    )

}
