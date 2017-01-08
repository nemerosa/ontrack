package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.AccountGroupMapping;
import net.nemerosa.ontrack.model.security.AccountGroupMappingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryAdminAccountGroupMappings implements GQLRootQuery {

    private static final String MAPPING_TYPE_ARGUMENT = "type";
    private static final String MAPPING_NAME_ARGUMENT = "name";
    private static final String MAPPING_GROUP_ARGUMENT = "group";

    private final AccountGroupMappingService accountGroupMappingService;
    private final GQLTypeAccountGroupMapping accountGroupMapping;

    @Autowired
    public GQLRootQueryAdminAccountGroupMappings(
            AccountGroupMappingService accountGroupMappingService,
            GQLTypeAccountGroupMapping accountGroupMapping) {
        this.accountGroupMappingService = accountGroupMappingService;
        this.accountGroupMapping = accountGroupMapping;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("accountGroupMappings")
                .type(stdList(accountGroupMapping.getType()))
                .argument(a -> a.name(MAPPING_TYPE_ARGUMENT)
                        .description("Mapping type")
                        .type(new GraphQLNonNull(GraphQLString)))
                .argument(a -> a.name(MAPPING_NAME_ARGUMENT)
                        .description("Mapping name")
                        .type(GraphQLString))
                .argument(a -> a.name(MAPPING_GROUP_ARGUMENT)
                        .description("Group name")
                        .type(GraphQLString))
                .dataFetcher(adminAccountGroupMappingsFetcher())
                .build();
    }

    private DataFetcher adminAccountGroupMappingsFetcher() {
        return environment -> {
            Predicate<AccountGroupMapping> filter = agm -> true;
            // Filter on name
            Optional<String> nameArgument = GraphqlUtils.getStringArgument(environment, MAPPING_NAME_ARGUMENT);
            if (nameArgument.isPresent()) {
                filter = filter.and(agm ->
                        StringUtils.equals(nameArgument.get(), agm.getName())
                );
            }
            // Filter on group
            Optional<String> groupArgument = GraphqlUtils.getStringArgument(environment, MAPPING_GROUP_ARGUMENT);
            if (groupArgument.isPresent()) {
                filter = filter.and(agm ->
                        StringUtils.equals(groupArgument.get(), agm.getGroup().getName())
                );
            }
            // List
            return accountGroupMappingService.getMappings(
                    GraphqlUtils.getStringArgument(environment, MAPPING_TYPE_ARGUMENT)
                            .orElseThrow(() -> new IllegalStateException("Required argument: " + MAPPING_TYPE_ARGUMENT))
            )
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        };
    }

}
