package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.security.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryAdminGlobalRoles implements GQLRootQuery {

    private final GQLTypeGlobalRole globalRole;
    private final RolesService rolesService;

    @Autowired
    public GQLRootQueryAdminGlobalRoles(GQLTypeGlobalRole globalRole, RolesService rolesService) {
        this.globalRole = globalRole;
        this.rolesService = rolesService;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("globalRoles")
                .description("List of global security roles")
                .type(stdList(globalRole.getType()))
                .dataFetcher(environment -> rolesService.getGlobalRoles())
                .build();
    }

}
