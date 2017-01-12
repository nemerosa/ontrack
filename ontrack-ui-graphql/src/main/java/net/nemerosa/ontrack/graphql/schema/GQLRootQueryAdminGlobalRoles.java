package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.exceptions.RoleNotFoundException;
import net.nemerosa.ontrack.model.security.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static graphql.Scalars.GraphQLString;
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
                .argument(a -> a.name("role")
                        .description("Filter by role name")
                        .type(GraphQLString)
                )
                .dataFetcher(environment -> GraphqlUtils.getStringArgument(environment, "role")
                        .map(name -> Collections.singletonList(
                                rolesService.getGlobalRole(name)
                                        .orElseThrow(() -> new RoleNotFoundException(name))
                        ))
                        .orElse(rolesService.getGlobalRoles()))
                .build();
    }

}
