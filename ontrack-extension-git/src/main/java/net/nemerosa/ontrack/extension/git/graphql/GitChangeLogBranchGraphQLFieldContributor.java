package net.nemerosa.ontrack.extension.git.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher;

@Component
public class GitChangeLogBranchGraphQLFieldContributor implements GQLProjectEntityFieldContributor {

    private final GitChangeLogGQLType gitChangeLogGQLType;
    private final StructureService structureService;
    private final GitService gitService;

    @Autowired
    public GitChangeLogBranchGraphQLFieldContributor(GitChangeLogGQLType gitChangeLogGQLType, StructureService structureService, GitService gitService) {
        this.gitChangeLogGQLType = gitChangeLogGQLType;
        this.structureService = structureService;
        this.gitService = gitService;
    }

    @Override
    public List<GraphQLFieldDefinition> getFields(Class<? extends ProjectEntity> projectEntityClass, ProjectEntityType projectEntityType) {
        return Collections.singletonList(
                GraphQLFieldDefinition.newFieldDefinition()
                        .name("gitChangeLog")
                        .argument(a -> a.name("from")
                                .description("Name of the build to start the change log with")
                                .type(new GraphQLNonNull(GraphQLString))
                        )
                        .argument(a -> a.name("to")
                                .description("Name of the build to end the change log with")
                                .type(new GraphQLNonNull(GraphQLString))
                        )
                        .type(gitChangeLogGQLType.getTypeRef())
                        .dataFetcher(gitChangeLogFetcher())
                        .build()
        );
    }

    private DataFetcher gitChangeLogFetcher() {
        return fetcher(
                Branch.class,
                (DataFetchingEnvironment environment, Branch branch) -> {
                    String from = GraphqlUtils.getStringArgument(environment, "from").orElseThrow(() -> new IllegalStateException("From argument is required."));
                    String to = GraphqlUtils.getStringArgument(environment, "to").orElseThrow(() -> new IllegalStateException("To argument is required."));
                    Build fromBuild = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), from)
                            .orElseThrow(() -> new BuildNotFoundException(branch.getProject().getName(), branch.getName(), from));
                    Build toBuild = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), to)
                            .orElseThrow(() -> new BuildNotFoundException(branch.getProject().getName(), branch.getName(), to));
                    return gitService.changeLog(
                            new BuildDiffRequest(
                                    fromBuild.getId(),
                                    toBuild.getId()
                            )
                    );
                }
        );
    }

}
