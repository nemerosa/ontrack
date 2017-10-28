package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import lombok.Data;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountGroup;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeProjectAuthorization implements GQLType {

    public static final String PROJECT_AUTHORIZATION = "ProjectAuthorization";

    @Override
    public GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(PROJECT_AUTHORIZATION);
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return newObject()
                .name(PROJECT_AUTHORIZATION)
                .field(GraphqlUtils.stringField("id", "ID of the role"))
                .field(GraphqlUtils.nameField("Unique name for the role"))
                .field(GraphqlUtils.stringField("description", "Description of the role"))
                // List of groups
                .field(f -> f.name("groups")
                        .description("List of groups having this role")
                        .type(stdList(new GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP)))
                )
                // List of accounts
                .field(f -> f.name("accounts")
                        .description("List of accounts having this role")
                        .type(stdList(new GraphQLTypeReference(GQLTypeAccount.ACCOUNT)))
                )
                // OK
                .build();
    }

    @Data
    public static class Model {
        private final String id;
        private final String name;
        private final String description;
        private final Collection<AccountGroup> groups;
        private final Collection<Account> accounts;
    }

}
