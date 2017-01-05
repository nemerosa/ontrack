package net.nemerosa.ontrack.extension.git.graphql;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.extension.git.model.GitChangeLog;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.graphql.schema.GQLType;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher;

/**
 * @see net.nemerosa.ontrack.extension.git.model.GitChangeLog
 */
@Component
public class GitChangeLogGQLType implements GQLType {

    private final GitUICommitGQLType gitUICommitGQLType;
    private final GitService gitService;

    @Autowired
    public GitChangeLogGQLType(GitUICommitGQLType gitUICommitGQLType, GitService gitService) {
        this.gitUICommitGQLType = gitUICommitGQLType;
        this.gitService = gitService;
    }

    @Override
    public GraphQLObjectType getType() {
        return GraphQLObjectType.newObject()
                .name("GitChangeLog")
                // Commits
                .field(f -> f.name("commits")
                        .description("List of commits in the change log")
                        .type(GraphqlUtils.stdList(gitUICommitGQLType.getType()))
                        .dataFetcher(fetcher(
                                GitChangeLog.class,
                                changeLog -> gitService.getChangeLogCommits(changeLog).getCommits()
                        ))
                )
                // TODO Issues
                // TODO File changes
                // OK
                .build();
    }

}
