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
public class GQLRootQueryAdminAccountGroups implements GQLRootQuery {

    public static final String ID_ARGUMENT = "id";

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
                .argument(
                        newArgument()
                                .name(ID_ARGUMENT)
                                .description("Searching by ID")
                                .type(GraphQLInt)
                                .build()
                )
                .dataFetcher(adminAccountGroupsFetcher())
                .build();
    }

    private DataFetcher adminAccountGroupsFetcher() {
        return environment -> {
            Integer id = environment.getArgument(ID_ARGUMENT);
            if (id != null) {
                checkArgList(environment, ID_ARGUMENT);
                return Collections.singletonList(
                        accountService.getAccountGroup(ID.of(id))
                );
            } else {
                return accountService.getAccountGroups();
            }
        };
    }

}
