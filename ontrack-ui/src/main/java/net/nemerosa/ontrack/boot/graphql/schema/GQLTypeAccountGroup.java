package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.fetcher;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

/**
 * @see AccountGroup
 */
@Component
public class GQLTypeAccountGroup implements GQLType {

    public static final String ACCOUNT_GROUP = "AccountGroup";

    public static final String ACCOUNTS_FIELD = "accounts";

    private final AccountService accountService;
    private final GQLTypeGlobalRole globalRole;

    @Autowired
    public GQLTypeAccountGroup(AccountService accountService, GQLTypeGlobalRole globalRole) {
        this.accountService = accountService;
        this.globalRole = globalRole;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(ACCOUNT_GROUP)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                // Associated accounts
                .field(field -> field.name(ACCOUNTS_FIELD)
                        .description("List of associated accounts")
                        .type(stdList(new GraphQLTypeReference(GQLTypeAccount.ACCOUNT)))
                        .dataFetcher(fetcher(AccountGroup.class, this::getAccountsForGroup))
                )
                // Global role
                .field(field -> field.name("globalRole")
                        .description("Global role for the account group")
                        .type(globalRole.getType())
                        .dataFetcher(fetcher(
                                AccountGroup.class,
                                group -> accountService.getGlobalRoleForAccountGroup(group).orElse(null)
                        ))
                )
                // OK
                .build();
    }

    private List<Account> getAccountsForGroup(AccountGroup accountGroup) {
        return accountService.getAccountsForGroup(accountGroup);
    }
}
