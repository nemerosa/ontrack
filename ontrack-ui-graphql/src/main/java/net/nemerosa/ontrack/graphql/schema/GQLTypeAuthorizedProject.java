package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.model.security.ProjectRoleAssociation;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher;

/**
 * @see net.nemerosa.ontrack.model.security.ProjectRoleAssociation
 */
@Component
public class GQLTypeAuthorizedProject implements GQLType {

    public static final String AUTHORIZED_PROJECT = "AuthorizedProject";

    private final GQLTypeProjectRole projectRole;
    private final StructureService structureService;

    @Autowired
    public GQLTypeAuthorizedProject(GQLTypeProjectRole projectRole, StructureService structureService) {
        this.projectRole = projectRole;
        this.structureService = structureService;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(AUTHORIZED_PROJECT)
                .field(
                        newFieldDefinition()
                                .name("role")
                                .description("Role for the project")
                                .type(new GraphQLNonNull(projectRole.getType()))
                                .dataFetcher(fetcher(ProjectRoleAssociation.class, ProjectRoleAssociation::getProjectRole))
                                .build()
                )
                .field(
                        newFieldDefinition()
                                .name("project")
                                .description("Authorized project")
                                .type(new GraphQLNonNull(new GraphQLTypeReference(GQLTypeProject.PROJECT)))
                                .dataFetcher(fetcher(
                                        ProjectRoleAssociation.class,
                                        pra -> structureService.getProject(ID.of(pra.getProjectId()))
                                ))
                                .build()
                )
                .build();
    }

}
