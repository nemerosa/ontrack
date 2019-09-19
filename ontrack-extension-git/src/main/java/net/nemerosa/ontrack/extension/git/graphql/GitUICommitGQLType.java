package net.nemerosa.ontrack.extension.git.graphql;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.extension.git.model.GitUICommit;
import net.nemerosa.ontrack.graphql.schema.GQLType;
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache;
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter;
import org.springframework.stereotype.Component;

/**
 * @see net.nemerosa.ontrack.extension.git.model.GitUICommit
 */
@Component
public class GitUICommitGQLType implements GQLType {

    @Override
    public String getTypeName() {
        return GitUICommit.class.getSimpleName();
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return GraphQLBeanConverter.INSTANCE.asObjectType(GitUICommit.class, cache);
    }

}
