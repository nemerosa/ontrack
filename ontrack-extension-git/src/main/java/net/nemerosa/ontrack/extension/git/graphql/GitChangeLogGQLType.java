package net.nemerosa.ontrack.extension.git.graphql;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
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

    public static final String GIT_CHANGE_LOG = "GitChangeLog";

    private final GitUICommitGQLType gitUICommitGQLType;
    private final GitService gitService;

    @Autowired
    public GitChangeLogGQLType(GitUICommitGQLType gitUICommitGQLType, GitService gitService) {
        this.gitUICommitGQLType = gitUICommitGQLType;
        this.gitService = gitService;
    }

    @Override
    public GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(GIT_CHANGE_LOG);
    }

    @Override
    public GraphQLObjectType createType() {
        return GraphQLObjectType.newObject()
                .name(GIT_CHANGE_LOG)
                // Commits
                .field(f -> f.name("commits")
                        .description("List of commits in the change log")
                        .type(GraphqlUtils.stdList(gitUICommitGQLType.getTypeRef()))
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
