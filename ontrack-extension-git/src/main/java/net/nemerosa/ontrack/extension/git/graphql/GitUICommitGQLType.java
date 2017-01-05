package net.nemerosa.ontrack.extension.git.graphql;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.graphql.schema.GQLType;
import org.springframework.stereotype.Component;

/**
 * @see net.nemerosa.ontrack.extension.git.model.GitUICommit
 */
@Component
public class GitUICommitGQLType implements GQLType {

    @Override
    public GraphQLObjectType getType() {
        return GraphQLObjectType.newObject()
                .name("GitUICommit")
                // TODO Fields
                // OK
                .build();
    }

}
