package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.checkArgList;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;
import static org.apache.commons.lang3.StringUtils.contains;

@Component
public class GQLRootQueryAdminAccounts implements GQLRootQuery {

    public static final String ID_ARGUMENT = "id";
    public static final String NAME_ARGUMENT = "name";
    public static final String GROUP_ARGUMENT = "group";
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
                                .name(ID_ARGUMENT)
                                .description("Searching by ID")
                                .type(GraphQLInt)
                                .build()
                )
                .argument(
                        newArgument()
                                .name(NAME_ARGUMENT)
                                .description("Searching by looking for a string in the name or the full name")
                                .type(GraphQLString)
                                .build()
                )
                .argument(
                        newArgument()
                                .name(GROUP_ARGUMENT)
                                .description("Searching by looking for a string in one of the groups the account belongs to")
                                .type(GraphQLString)
                                .build()
                )
                .dataFetcher(adminAccountsFetcher())
                .build();
    }

    private DataFetcher adminAccountsFetcher() {
        return environment -> {
            Integer id = environment.getArgument(ID_ARGUMENT);
            String name = environment.getArgument(NAME_ARGUMENT);
            String group = environment.getArgument(GROUP_ARGUMENT);
            if (id != null) {
                checkArgList(environment, ID_ARGUMENT);
                return Collections.singletonList(
                        accountService.getAccount(ID.of(id))
                );
            } else {
                Predicate<Account> filter = a -> true;
                // Filter by name
                if (StringUtils.isNotBlank(name)) {
                    filter = filter.and(
                            account -> contains(account.getName(), name) || contains(account.getFullName(), name)
                    );
                }
                // Filter by group
                if (StringUtils.isNotBlank(group)) {
                    filter = filter.and(
                            account -> account.getAccountGroups().stream().anyMatch(
                                    grp -> contains(grp.getName(), group)
                            )
                    );
                }
                // Getting the list
                return accountService.getAccounts().stream()
                        .filter(filter)
                        .collect(Collectors.toList());
            }
        };
    }

}
