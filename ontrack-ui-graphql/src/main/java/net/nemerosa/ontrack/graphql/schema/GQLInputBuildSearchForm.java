package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter;
import net.nemerosa.ontrack.model.structure.BuildSearchForm;
import org.springframework.stereotype.Component;

@Component
public class GQLInputBuildSearchForm implements GQLInputType<BuildSearchForm> {

    @Override
    public GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(BuildSearchForm.class.getSimpleName());
    }

    @Override
    public GraphQLInputType createInputType() {
        return GraphQLBeanConverter.INSTANCE.asInputType(BuildSearchForm.class);
    }

    @Override
    public BuildSearchForm convert(Object argument) {
        if (argument == null) {
            return new BuildSearchForm().withMaximumCount(10);
        } else {
            return GraphQLBeanConverter.INSTANCE.asObject(argument, BuildSearchForm.class);
        }
    }

}
