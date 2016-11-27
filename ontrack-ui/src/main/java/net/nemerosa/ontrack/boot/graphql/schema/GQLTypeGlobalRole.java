package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeGlobalRole extends AbstractGQLType {

    public static final String GLOBAL_ROLE = "GlobalRole";

    @Autowired
    public GQLTypeGlobalRole(URIBuilder uriBuilder, SecurityService securityService) {
        super(uriBuilder, securityService);
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(GLOBAL_ROLE)
                .field(GraphqlUtils.stringField("id", "ID of the role"))
                .field(GraphqlUtils.nameField("Unique name for the role"))
                .field(GraphqlUtils.stringField("description", "Description of the role"))
                .build();
    }

}
