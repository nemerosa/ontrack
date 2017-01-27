package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.GlobalRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

/**
 * @see net.nemerosa.ontrack.model.security.GlobalRole
 */
@Component
public class GQLTypeGlobalRole implements GQLType {

    public static final String GLOBAL_ROLE = "GlobalRole";

    private final AccountService accountService;

    @Autowired
    public GQLTypeGlobalRole(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(GLOBAL_ROLE)
                .field(GraphqlUtils.stringField("id", "ID of the role"))
                .field(GraphqlUtils.nameField("Unique name for the role"))
                .field(GraphqlUtils.stringField("description", "Description of the role"))
                // List of groups
                .field(f -> f.name("groups")
                        .description("List of groups having this role")
                        .type(stdList(new GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP)))
                        .dataFetcher(fetcher(GlobalRole.class, accountService::findAccountGroupsByGlobalRole))
                )
                // List of accounts
                .field(f -> f.name("accounts")
                        .description("List of accounts having this role")
                        .type(stdList(new GraphQLTypeReference(GQLTypeAccount.ACCOUNT)))
                        .dataFetcher(fetcher(GlobalRole.class, accountService::findAccountsByGlobalRole))
                )
                // OK
                .build();
    }

}
