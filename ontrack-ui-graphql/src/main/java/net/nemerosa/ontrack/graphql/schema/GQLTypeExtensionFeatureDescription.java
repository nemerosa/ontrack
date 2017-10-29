package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Description of a {@link net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription}.
 */
@Component
public class GQLTypeExtensionFeatureDescription implements GQLType {

    public static final String EXTENSION_FEATURE_DESCRIPTION = "ExtensionFeatureDescription";

    @Override
    public String getTypeName() {
        return EXTENSION_FEATURE_DESCRIPTION;
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return newObject()
                .name(EXTENSION_FEATURE_DESCRIPTION)
                .field(f -> f.name("id")
                        .description("Feature ID")
                        .type(GraphQLString)
                )
                .field(f -> f.name("name")
                        .description("Feature name")
                        .type(GraphQLString)
                )
                .field(f -> f.name("description")
                        .description("Feature description")
                        .type(GraphQLString)
                )
                .field(f -> f.name("version")
                        .description("Feature version")
                        .type(GraphQLString)
                )
                // OK
                .build();
    }

}
