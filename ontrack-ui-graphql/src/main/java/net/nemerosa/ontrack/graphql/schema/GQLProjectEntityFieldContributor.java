package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.List;

/**
 * Contributes a list of fields to a project entity.
 */
public interface GQLProjectEntityFieldContributor {

    /**
     * List of field contributions
     *
     * @param projectEntityClass Class of project entity
     * @param projectEntityType  Type of project entity
     * @return List of fields to call. <code>null</code> is accepted.
     */
    List<GraphQLFieldDefinition> getFields(Class<? extends ProjectEntity> projectEntityClass, ProjectEntityType projectEntityType);

}
