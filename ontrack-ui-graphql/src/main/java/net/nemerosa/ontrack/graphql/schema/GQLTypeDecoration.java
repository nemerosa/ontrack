package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.Decoration;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final GQLTypeExtensionFeatureDescription extensionFeatureDescription;

    @Autowired
    public GQLTypeDecoration(GQLTypeExtensionFeatureDescription extensionFeatureDescription) {
        this.extensionFeatureDescription = extensionFeatureDescription;
    }

    @Override
    public GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(DECORATION);
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
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
                // Feature
                .field(f -> f.name("feature")
                        .description("Extension feature")
                        .type(extensionFeatureDescription.getTypeRef())
                )
                // OK
                .build();
    }

    private String getData(Decoration<?> p) {
        Object value = p.getData();
        return value == null ? null : JsonUtils.toJSONString(value);
    }

}
