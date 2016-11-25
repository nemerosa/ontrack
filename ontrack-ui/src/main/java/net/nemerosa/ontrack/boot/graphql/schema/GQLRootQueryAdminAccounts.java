package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.checkArgList;
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
                .argument(
                        newArgument()
                                .name("id")
                                .description("Searching by ID")
                                .type(GraphQLInt)
                                .build()
                )
                .dataFetcher(adminAccountsFetcher())
                .build();
    }

    private DataFetcher adminAccountsFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            if (id != null) {
                checkArgList(environment, "id");
                return Collections.singletonList(
                        accountService.getAccount(ID.of(id))
                );
            } else {
                return accountService.getAccounts();
            }
        };
    }

}
