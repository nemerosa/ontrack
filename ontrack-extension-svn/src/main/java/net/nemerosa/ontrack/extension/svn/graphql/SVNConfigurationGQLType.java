package net.nemerosa.ontrack.extension.svn.graphql;

import com.google.common.collect.ImmutableSet;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.graphql.schema.GQLType;
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter;
import org.springframework.stereotype.Component;

@Component
public class SVNConfigurationGQLType implements GQLType {

    @Override
    public GraphQLObjectType getType() {
        return GraphQLBeanConverter.asObjectType(SVNConfiguration.class, ImmutableSet.of("password"));
    }
}
