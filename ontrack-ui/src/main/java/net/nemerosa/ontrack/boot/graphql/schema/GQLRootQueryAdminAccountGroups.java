package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.security.AccountGroup;
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
public class GQLRootQueryAdminAccountGroups implements GQLRootQuery {

    public static final String ID_ARGUMENT = "id";
    public static final String NAME_ARGUMENT = "name";

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
                .argument(
                        newArgument()
                                .name(NAME_ARGUMENT)
                                .description("Searching by looking for a string in the name or the description")
                                .type(GraphQLString)
                                .build()
                )
                .dataFetcher(adminAccountGroupsFetcher())
                .build();
    }

    private DataFetcher adminAccountGroupsFetcher() {
        return environment -> {
            Integer id = environment.getArgument(ID_ARGUMENT);
            String name = environment.getArgument(NAME_ARGUMENT);
            if (id != null) {
                checkArgList(environment, ID_ARGUMENT);
                return Collections.singletonList(
                        accountService.getAccountGroup(ID.of(id))
                );
            } else {
                Predicate<AccountGroup> filter = a -> true;
                // Filter by name
                if (StringUtils.isNotBlank(name)) {
                    filter = filter.and(
                            group -> contains(group.getName(), name) || contains(group.getDescription(), name)
                    );
                }
                // Getting the list
                return accountService.getAccountGroups().stream()
                        .filter(filter)
                        .collect(Collectors.toList());
            }
        };
    }

}
