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

@Component
public class GQLTypeAccountGroup implements GQLType {

    public static final String ACCOUNT_GROUP = "AccountGroup";

    public static final String ACCOUNTS_FIELD = "accounts";

    private final AccountService accountService;

    @Autowired
    public GQLTypeAccountGroup(AccountService accountService) {
        this.accountService = accountService;
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
                // OK
                .build();
    }

    private List<Account> getAccountsForGroup(AccountGroup accountGroup) {
        return accountService.getAccountsForGroup(accountGroup);
    }
}
