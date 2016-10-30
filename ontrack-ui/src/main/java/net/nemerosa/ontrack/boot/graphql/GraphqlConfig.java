package net.nemerosa.ontrack.boot.graphql;

import graphql.relay.SimpleListConnection;
import graphql.schema.*;
import net.nemerosa.ontrack.boot.graphql.relay.Relay;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Configuration
public class GraphqlConfig {

    public static final String QUERY = "Query";
    public static final String PROJECT_ENTITY = "ProjectEntity";
    public static final String PROJECT = "Project";
    public static final String BRANCH = "Branch";
    public static final String BUILD = "Build";
    public static final String PROMOTION_LEVEL = "PromotionLevel";
    public static final String PROMOTION_RUN = "PromotionRun";

    @Autowired
    private StructureService structureService;

    @Autowired
    private BuildFilterService buildFilterService;

    /**
     * GraphQL schema definition
     */
    @Bean
    @Qualifier("ontrack")
    public GraphQLSchema grapqlSchema() {
        return GraphQLSchema.newSchema()
                .query(queryType())
                .build();
    }

    private List<GraphQLFieldDefinition> projectEntityInterfaceFields() {
        return Arrays.asList(
                GraphqlUtils.idField(),
                GraphqlUtils.nameField(),
                GraphqlUtils.descriptionField()
        );
    }

    private GraphQLInterfaceType projectEntityInterface() {
        return GraphQLInterfaceType.newInterface()
                .name(PROJECT_ENTITY)
                .fields(projectEntityInterfaceFields())
                // TODO Properties
                // TODO Type resolver not set, but it should
                .typeResolver(new TypeResolverProxy())
                // OK
                .build();
    }

    // TODO Define a ProjectEntity interface

    private GraphQLObjectType buildType() {
        return newObject()
                .name(BUILD)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // TODO Signature
                // Promotion runs
                .field(
                        newFieldDefinition()
                                .name("promotionRuns")
                                .description("Promotions for this build")
                                .argument(
                                        GraphQLArgument.newArgument()
                                                .name("promotion")
                                                .description("Name of the promotion level")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .type(GraphqlUtils.stdList(new GraphQLTypeReference(PROMOTION_RUN)))
                                .dataFetcher(buildPromotionRunsFetcher())
                                .build()
                )
                // TODO Validation runs
                // TODO Build properties
                // OK
                .build();
    }

    private GraphQLObjectType branchType() {
        return newObject()
                .name(BRANCH)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                .field(GraphqlUtils.disabledField())
                .field(
                        newFieldDefinition()
                                .name("type")
                                .type(GraphqlUtils.newEnumType(BranchType.class))
                                .build()
                )
                // TODO Events: branch creation
                // Promotion levels
                .field(
                        newFieldDefinition()
                                .name("promotionLevels")
                                .type(GraphqlUtils.stdList(promotionLevelType()))
                                .dataFetcher(branchPromotionLevelsFetcher())
                                .build()
                )
                // TODO Validation stamps
                // Builds for the branch
                .field(
                        newFieldDefinition()
                                .name("builds")
                                .type(GraphqlUtils.connectionList(buildType()))
                                // TODO Build filtering
                                .argument(
                                        GraphQLArgument.newArgument()
                                                .name("count")
                                                .description("Maximum number of builds to return")
                                                .type(GraphQLInt)
                                                .build()
                                )
                                .dataFetcher(branchBuildsFetcher())
                                .build()
                )
                // OK
                .build();
    }

    private GraphQLObjectType promotionLevelType() {
        return newObject()
                .name(PROMOTION_LEVEL)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // TODO Image
                // Promotion runs
                .field(
                        newFieldDefinition()
                                .name("promotionRuns")
                                .description("List of runs for this promotion")
                                .type(GraphqlUtils.connectionList(promotionRunType()))
                                .argument(Relay.getConnectionFieldArguments())
                                .dataFetcher(promotionLevelPromotionRunsFetcher())
                                .build()
                )
                // OK
                .build();
    }

    private GraphQLObjectType promotionRunType() {
        return newObject()
                .name(PROMOTION_RUN)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // Build
                .field(
                        newFieldDefinition()
                                .name("build")
                                .description("Associated build")
                                .type(new GraphQLNonNull(new GraphQLTypeReference(BUILD)))
                                .build()
                )
                // Promotion level
                .field(
                        newFieldDefinition()
                                .name("promotionLevel")
                                .description("Associated promotion level")
                                .type(new GraphQLNonNull(new GraphQLTypeReference(PROMOTION_LEVEL)))
                                .build()
                )
                // TODO Signature
                // OK
                .build();
    }


    private GraphQLObjectType projectType() {
        return newObject()
                .name(PROJECT)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                .field(GraphqlUtils.disabledField())
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(GraphqlUtils.stdList(branchType()))
                                .argument(
                                        GraphQLArgument.newArgument()
                                                .name("name")
                                                .description("Exact name of the branch to look for.")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .dataFetcher(projectBranchesFetcher())
                                .build()
                )
                // TODO Events: project creation
                // TODO Project properties
                // OK
                .build();
    }

    private GraphQLObjectType queryType() {
        return newObject()
                .name(QUERY)
                // Project
                .field(
                        newFieldDefinition()
                                .name("projects")
                                .type(GraphqlUtils.stdList(projectType()))
                                .argument(
                                        GraphQLArgument.newArgument()
                                                .name("id")
                                                .description("ID of the project to look for")
                                                .type(GraphQLInt)
                                                .build()
                                )
                                .dataFetcher(projectFetcher())
                                .build()
                )
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(GraphqlUtils.stdList(branchType()))
                                // TODO Branch search
                                .build()
                )
                // TODO Extension contributions
                // OK
                .build();
    }

    private DataFetcher projectBranchesFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Project) {
                Project project = (Project) source;
                String name = environment.getArgument("name");
                // TODO Combined filter
                // TODO Other criterias
                if (name != null) {
                    Optional<Branch> oBranch = structureService.findBranchByName(project.getName(), name);
                    if (oBranch.isPresent()) {
                        return Collections.singletonList(oBranch.get());
                    } else {
                        return Collections.emptyList();
                    }
                } else {
                    return structureService.getBranchesForProject(
                            project.getId()
                    );
                }
            } else {
                return Collections.emptyList();
            }
        };
    }

    private DataFetcher projectFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            if (id != null) {
                // TODO No other argument is expected
                // Fetch by ID
                Project project = structureService.getProject(ID.of(id));
                // As list
                return Collections.singletonList(project);
            }
            // TODO Other criterias
            // Whole list
            else {
                return structureService.getProjectList();
            }
        };
    }

    private DataFetcher branchBuildsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Branch) {
                Branch branch = (Branch) source;
                // Count
                int count = GraphqlUtils.getIntArgument(environment, "count").orElse(10);
                // TODO Build filtering
                BuildFilter buildFilter = buildFilterService.standardFilter(count).build();
                // Result
                List<Build> builds = structureService.getFilteredBuilds(
                        branch.getId(),
                        buildFilter
                );
                // As a connection list
                return new SimpleListConnection(builds).get(environment);
            } else {
                return Collections.emptyList();
            }
        };
    }

    private DataFetcher promotionLevelPromotionRunsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof PromotionLevel) {
                PromotionLevel promotionLevel = (PromotionLevel) source;
                // Gets all the promotion runs
                List<PromotionRun> promotionRuns = structureService.getPromotionRunsForPromotionLevel(promotionLevel.getId());
                // As a connection list
                return new SimpleListConnection(promotionRuns).get(environment);
            } else {
                return Collections.emptyList();
            }
        };
    }

    private DataFetcher buildPromotionRunsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Build) {
                Build build = (Build) source;
                // Promotion filter
                String promotion = GraphqlUtils.getStringArgument(environment, "promotion").orElse(null);
                if (promotion != null) {
                    // Gets the promotion level
                    PromotionLevel promotionLevel = structureService.findPromotionLevelByName(
                            build.getProject().getName(),
                            build.getBranch().getName(),
                            promotion
                    ).orElseThrow(() -> new PromotionLevelNotFoundException(
                            build.getProject().getName(),
                            build.getBranch().getName(),
                            promotion
                    ));
                    // Gets promotion runs for this promotion level
                    return structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel);
                } else {
                    // Gets all the promotion runs
                    return structureService.getPromotionRunsForBuild(build.getId());
                }
            } else {
                return Collections.emptyList();
            }
        };
    }

    private DataFetcher branchPromotionLevelsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Branch) {
                Branch branch = (Branch) source;
                return structureService.getPromotionLevelListForBranch(branch.getId());
            } else {
                return Collections.emptyList();
            }
        };
    }

}
