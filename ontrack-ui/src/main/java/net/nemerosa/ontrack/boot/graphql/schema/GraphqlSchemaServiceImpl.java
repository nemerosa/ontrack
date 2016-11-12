package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import net.nemerosa.ontrack.common.CachedSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLObjectType.newObject;

@Service
public class GraphqlSchemaServiceImpl implements GraphqlSchemaService {

    public static final String QUERY = "Query";

    private final CachedSupplier<GraphQLSchema> schemaSupplier = CachedSupplier.of(this::createSchema);

    private final List<GQLRootQuery> rootQueries;

    @Autowired
    public GraphqlSchemaServiceImpl(List<GQLRootQuery> rootQueries) {
        this.rootQueries = rootQueries;
    }

    @Override
    public GraphQLSchema getSchema() {
        return schemaSupplier.get();
    }

    private GraphQLSchema createSchema() {
        return GraphQLSchema.newSchema()
                .query(createQueryType())
                .build();
    }

    private GraphQLObjectType createQueryType() {
        return newObject()
                .name(QUERY)
                // Root queries
                .fields(
                        rootQueries.stream()
                                .map(GQLRootQuery::getFieldDefinition)
                                .collect(Collectors.toList())
                )
                // FIXME Branches
//                .field(
//                        newFieldDefinition()
//                                .name("branches")
//                                .type(stdList(branchType()))
//                                .argument(
//                                        newArgument()
//                                                .name("id")
//                                                .description("ID of the branch to look for")
//                                                .type(new GraphQLNonNull(GraphQLInt))
//                                                .build()
//                                )
//                                .dataFetcher(branchFetcher())
//                                .build()
//                )
                // TODO Builds
                // TODO Promotion levels
                // TODO Promotion runs
                // TODO Validation stamps
                // FIXME Validation runs
//                .field(
//                        newFieldDefinition()
//                                .name("validationRuns")
//                                .type(stdList(validationRunType()))
//                                .argument(
//                                        newArgument()
//                                                .name("id")
//                                                .description("ID of the validation run to look for")
//                                                .type(new GraphQLNonNull(GraphQLInt))
//                                                .build()
//                                )
//                                .dataFetcher(validationRunFetcher())
//                                .build()
//                )
                // TODO Extension contributions
                // OK
                .build();
    }

}
