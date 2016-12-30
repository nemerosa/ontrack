package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.Decoration;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Description of a {@link net.nemerosa.ontrack.model.structure.Decoration}.
 */
@Component
public class GQLTypeDecoration implements GQLType {

    public static final String DECORATION = "Decoration";

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(DECORATION)
                // Type
                .field(
                        newFieldDefinition()
                                .name("decorationType")
                                .description("Decoration type")
                                .type(GraphQLString)
                                .build()
                )
                // Value
                .field(
                        newFieldDefinition()
                                .name("data")
                                .description("JSON representation of the decoration data")
                                .type(GQLScalarJSON.INSTANCE)
                                .dataFetcher(GraphqlUtils.fetcher(Decoration.class, this::getData))
                                .build()
                )
                // Error
                .field(
                        newFieldDefinition()
                                .name("error")
                                .description("Any error message associated with the decoration")
                                .type(GraphQLString)
                                .build()
                )
                // OK
                .build();
    }

    private String getData(Decoration<?> p) {
        Object value = p.getData();
        return value == null ? null : JsonUtils.toJSONString(value);
    }

}
