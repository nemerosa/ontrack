package net.nemerosa.ontrack.extension.git.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLInt;

@Component
public class GitChangeLogGraphQLRootQuery implements GQLRootQuery {

    private final GitChangeLogGQLType gitChangeLogGQLType;
    private final GitService gitService;

    @Autowired
    public GitChangeLogGraphQLRootQuery(GitChangeLogGQLType gitChangeLogGQLType, GitService gitService) {
        this.gitChangeLogGQLType = gitChangeLogGQLType;
        this.gitService = gitService;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("gitChangeLog")
                .argument(a -> a.name("from")
                        .description("ID of the build to start the change log with")
                        .type(new GraphQLNonNull(GraphQLInt))
                )
                .argument(a -> a.name("to")
                        .description("ID of the build to end the change log with")
                        .type(new GraphQLNonNull(GraphQLInt))
                )
                .type(gitChangeLogGQLType.getTypeRef())
                .dataFetcher(gitChangeLogFetcher())
                .build();
    }

    private DataFetcher gitChangeLogFetcher() {
        return environment -> {
            int from = GraphqlUtils.getIntArgument(environment, "from").orElseThrow(
                    () -> new IllegalStateException("From argument is required")
            );
            int to = GraphqlUtils.getIntArgument(environment, "to").orElseThrow(
                    () -> new IllegalStateException("To argument is required")
            );
            return gitService.changeLog(new BuildDiffRequest(
                    ID.of(from),
                    ID.of(to)
            ));
        };
    }

}
