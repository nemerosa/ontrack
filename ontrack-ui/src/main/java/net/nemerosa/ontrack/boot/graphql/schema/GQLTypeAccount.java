package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeAccount extends AbstractGQLType {

    public static final String ACCOUNT = "Account";

    @Autowired
    public GQLTypeAccount(URIBuilder uriBuilder, SecurityService securityService) {
        super(uriBuilder, securityService);
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(ACCOUNT)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField("Unique name for the account"))
                .field(GraphqlUtils.stringField("fullName", "Full name of the account"))
                .field(GraphqlUtils.stringField("email", "Email of the account"))
                .field(newFieldDefinition()
                        .name("authenticationSource")
                        .description("Source of authentication (builtin, ldap, etc.)")
                        .type(GraphQLString)
                        .dataFetcher(environment -> ((Account) environment.getSource()).getAuthenticationSource().getId())
                        .build()
                )
                .field(GraphqlUtils.stringField("role", "Security role (admin or none)"))
                // TODO Groups
                .build();
    }
}
