package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryAdminAccounts implements GQLRootQuery {

    private final AccountService accountService;
    private final GQLTypeAccount account;

    @Autowired
    public GQLRootQueryAdminAccounts(AccountService accountService, GQLTypeAccount account) {
        this.accountService = accountService;
        this.account = account;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("accounts")
                .type(stdList(account.getType()))
                .dataFetcher(adminAccountsFetcher())
                .build();
    }

    private DataFetcher adminAccountsFetcher() {
        return environment -> accountService.getAccounts();
    }

}
