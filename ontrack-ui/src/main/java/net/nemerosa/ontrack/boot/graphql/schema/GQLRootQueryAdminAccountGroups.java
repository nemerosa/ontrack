package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryAdminAccountGroups implements GQLRootQuery {

    private final AccountService accountService;
    private final GQLTypeAccountGroup accountGroup;

    @Autowired
    public GQLRootQueryAdminAccountGroups(AccountService accountService, GQLTypeAccountGroup accountGroup) {
        this.accountService = accountService;
        this.accountGroup = accountGroup;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("accountGroups")
                .type(stdList(accountGroup.getType()))
                .dataFetcher(adminAccountGroupsFetcher())
                .build();
    }

    private DataFetcher adminAccountGroupsFetcher() {
        return environment -> accountService.getAccountGroups();
    }

}
