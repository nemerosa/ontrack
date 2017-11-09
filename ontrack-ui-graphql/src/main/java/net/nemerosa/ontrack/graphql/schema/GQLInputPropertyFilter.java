package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.PropertyType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class GQLInputPropertyFilter implements GQLInputType<PropertyFilter> {

    public static final String ARGUMENT_NAME = "withProperty";

    private final PropertyService propertyService;

    @Autowired
    public GQLInputPropertyFilter(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(PropertyFilter.class.getSimpleName());
    }

    @Override
    public GraphQLInputType createInputType() {
        return GraphQLBeanConverter.asInputType(PropertyFilter.class);
    }

    @Override
    public PropertyFilter convert(Object argument) {
        if (argument == null) {
            return null;
        } else {
            return GraphQLBeanConverter.asObject(argument, PropertyFilter.class);
        }
    }

    public GraphQLArgument asArgument() {
        return GraphQLArgument.newArgument()
                .name(ARGUMENT_NAME)
                .description("Filter on property type and optional value pattern.")
                .type(new GraphQLTypeReference(PropertyFilter.class.getSimpleName()))
                .build();
    }

    public Predicate<? super ProjectEntity> getFilter(PropertyFilter filter) {
        return e -> matchProperty(e, filter);
    }

    private <T> boolean matchProperty(ProjectEntity e, PropertyFilter filter) {
        PropertyType<T> type = propertyService.getPropertyTypeByName(filter.getType());
        Property<T> property = propertyService.getProperty(e, filter.getType());
        return !property.isEmpty() &&
                (
                        StringUtils.isBlank(filter.getValue()) ||
                                type.containsValue(property.getValue(), filter.getValue())
                );
    }

}
