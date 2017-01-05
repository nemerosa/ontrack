package net.nemerosa.ontrack.extension.git.graphql;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.graphql.schema.GQLType;
import org.springframework.stereotype.Component;

/**
 * @see net.nemerosa.ontrack.extension.git.model.GitChangeLog
 */
@Component
public class GitChangeLogGQLType implements GQLType {

    @Override
    public GraphQLObjectType getType() {
        return GraphQLObjectType.newObject()
                .name("GitChangeLog")
                // Commits
                .field(f -> f.name("commits")
                                .description("List of commits in the change log")
                        // TODO Type
                        // TODO Fetcher
                )
                // TODO Issues
                // TODO File changes
                // OK
                .build();
    }

}
