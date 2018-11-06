package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON;
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
    public String getTypeName() {
        return DECORATION;
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

}
