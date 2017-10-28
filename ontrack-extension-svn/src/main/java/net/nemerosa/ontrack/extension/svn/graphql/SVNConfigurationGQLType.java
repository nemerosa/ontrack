package net.nemerosa.ontrack.extension.svn.graphql;

import com.google.common.collect.ImmutableSet;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.graphql.schema.GQLType;
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter;
import org.springframework.stereotype.Component;

@Component
public class SVNConfigurationGQLType implements GQLType {

    @Override
    public GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(SVNConfiguration.class.getSimpleName());
    }

    @Override
    public GraphQLObjectType createType() {
        return GraphQLBeanConverter.asObjectType(SVNConfiguration.class, ImmutableSet.of("password", "descriptor", "credentials"));
    }
}
