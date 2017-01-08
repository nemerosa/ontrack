package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.AccountGroupMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryAdminAccountGroupMappings implements GQLRootQuery {

    private static final String MAPPING_TYPE_ARGUMENT = "type";

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
                .dataFetcher(adminAccountGroupMappingsFetcher())
                .build();
    }

    private DataFetcher adminAccountGroupMappingsFetcher() {
        return environment -> accountGroupMappingService.getMappings(
                GraphqlUtils.getStringArgument(environment, MAPPING_TYPE_ARGUMENT)
                        .orElseThrow(() -> new IllegalStateException("Required argument: " + MAPPING_TYPE_ARGUMENT))
        );
    }

}
