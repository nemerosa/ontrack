package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeAccountGroup extends AbstractGQLType {

    public static final String ACCOUNT_GROUP = "AccountGroup";

    @Autowired
    public GQLTypeAccountGroup(URIBuilder uriBuilder, SecurityService securityService) {
        super(uriBuilder, securityService);
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(ACCOUNT_GROUP)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                .build();
    }
}
