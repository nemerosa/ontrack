package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Description of a {@link net.nemerosa.ontrack.model.structure.Property}.
 */
@Component
public class GQLTypeProperty implements GQLType {

    public static final String PROPERTY = "Property";

    private final GQLTypePropertyType propertyType;

    @Autowired
    public GQLTypeProperty(GQLTypePropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public String getTypeName() {
        return PROPERTY;
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return newObject()
                .name(PROPERTY)
                // Type
                .field(
                        newFieldDefinition()
                                .name("type")
                                .description("Property type")
                                .type(propertyType.getTypeRef())
                                .dataFetcher(GraphqlUtils.fetcher(Property.class, Property::getTypeDescriptor))
                                .build()
                )
                // Value
                .field(
                        newFieldDefinition()
                                .name("value")
                                .description("JSON representation of the value")
                                .type(GQLScalarJSON.INSTANCE)
                                .dataFetcher(GraphqlUtils.fetcher(Property.class, this::getValue))
                                .build()
                )
                // Editable
                .field(
                        newFieldDefinition()
                                .name("editable")
                                .description("True is the field is editable")
                                .type(GraphQLBoolean)
                                .dataFetcher(GraphqlUtils.fetcher(Property.class, Property::isEditable))
                                .build()
                )
                // OK
                .build();
    }

    private String getValue(Property<?> p) {
        Object value = p.getValue();
        return value == null ? null : JsonUtils.toJSONString(value);
    }

}
