package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType

/**
 * Contributes a list of fields to a project entity.
 */
interface GQLProjectEntityFieldContributor {

    /**
     * List of field contributions
     *
     * @param projectEntityClass Class of project entity
     * @param projectEntityType  Type of project entity
     * @return List of fields to call. `null` is accepted.
     */
    fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>?

}
