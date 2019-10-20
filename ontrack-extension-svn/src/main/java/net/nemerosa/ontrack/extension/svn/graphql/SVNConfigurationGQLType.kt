package net.nemerosa.ontrack.extension.svn.graphql;

import com.google.common.collect.ImmutableSet;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.graphql.schema.GQLType;
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache;
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter;
import org.springframework.stereotype.Component;

@Component
public class SVNConfigurationGQLType implements GQLType {

    @Override
    public String getTypeName() {
        return SVNConfiguration.class.getSimpleName();
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return GraphQLBeanConverter.INSTANCE.asObjectType(SVNConfiguration.class, cache, ImmutableSet.of("password", "descriptor", "credentials"));
    }
}
