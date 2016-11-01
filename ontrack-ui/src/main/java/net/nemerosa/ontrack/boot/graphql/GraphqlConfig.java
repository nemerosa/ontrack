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

import static graphql.Scalars.*;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.GraphqlUtils.stdList;

@Configuration
public class GraphqlConfig {

    public static final String QUERY = "Query";
    public static final String PROJECT_ENTITY = "ProjectEntity";
    public static final String PROJECT = "Project";
    public static final String BRANCH = "Branch";
    public static final String BUILD = "Build";
    public static final String PROMOTION_LEVEL = "PromotionLevel";
    public static final String PROMOTION_RUN = "PromotionRun";
    public static final String VALIDATION_STAMP = "ValidationStamp";
    public static final String VALIDATION_RUN = "ValidationRun";
    public static final String VALIDATION_RUN_STATUS = "ValidationRunStatus";
    public static final String VALIDATION_RUN_STATUS_ID = "ValidationRunStatusID";

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
                                .type(stdList(new GraphQLTypeReference(PROMOTION_RUN)))
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
                                .type(stdList(promotionLevelType()))
                                .dataFetcher(branchPromotionLevelsFetcher())
                                .build()
                )
                // Validation stamps
                .field(
                        newFieldDefinition()
                                .name("validationStamps")
                                .type(stdList(validationStampType()))
                                .dataFetcher(branchValidationStampsFetcher())
                                .build()
                )
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

    private GraphQLObjectType validationStampType() {
        return newObject()
                .name(VALIDATION_STAMP)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // TODO Image
                // Validation runs
                .field(
                        newFieldDefinition()
                                .name("validationRuns")
                                .description("List of runs for this validation stamp")
                                .type(GraphqlUtils.connectionList(validationRunType()))
                                .argument(Relay.getConnectionFieldArguments())
                                .dataFetcher(validationStampValidationRunsFetcher())
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

    private GraphQLObjectType validationRunType() {
        return newObject()
                .name(VALIDATION_RUN)
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
                                .name("validationStamp")
                                .description("Associated validation stamp")
                                .type(new GraphQLNonNull(new GraphQLTypeReference(VALIDATION_STAMP)))
                                .build()
                )
                // Run order
                .field(
                        newFieldDefinition()
                                .name("runOrder")
                                .description("Run order")
                                .type(GraphQLInt)
                                .build()
                )
                // Validation statuses
                .field(
                        newFieldDefinition()
                                .name("validationRunStatuses")
                                .description("List of validation statuses")
                                .type(stdList(validationRunStatusType()))
                                .build()
                )
                // OK
                .build();
    }

    private GraphQLObjectType validationRunStatusType() {
        return newObject()
                .name(VALIDATION_RUN_STATUS)
                // TODO Signature
                // Status ID
                .field(
                        newFieldDefinition()
                                .name("statusID")
                                .description("Status ID")
                                .type(validationRunStatusIDType())
                                .build()
                )
                // Description
                .field(GraphqlUtils.descriptionField())
                // OK
                .build();
    }

    private GraphQLObjectType validationRunStatusIDType() {
        return newObject()
                .name(VALIDATION_RUN_STATUS_ID)
                // ID
                .field(
                        newFieldDefinition()
                                .name("id")
                                .description("Status ID")
                                .type(GraphQLString)
                                .build()
                )
                // Name
                .field(
                        newFieldDefinition()
                                .name("name")
                                .description("Status display name")
                                .type(GraphQLString)
                                .build()
                )
                // Root
                .field(
                        newFieldDefinition()
                                .name("root")
                                .description("Root status?")
                                .type(GraphQLBoolean)
                                .build()
                )
                // Passed
                .field(
                        newFieldDefinition()
                                .name("passed")
                                .description("Passing status?")
                                .type(GraphQLBoolean)
                                .build()
                )
                // Following statuses
                .field(
                        newFieldDefinition()
                                .name("followingStatuses")
                                .description("List of following statuses")
                                .type(stdList(GraphQLString))
                                .build()
                )
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
                                .type(stdList(branchType()))
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
                                .type(stdList(projectType()))
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
                                .type(stdList(branchType()))
                                // TODO Branch search
                                .build()
                )
                // TODO Builds
                // TODO Promotion levels
                // TODO Promotion runs
                // TODO Validation stamps
                // Validation runs
                .field(
                        newFieldDefinition()
                                .name("validationRuns")
                                .type(stdList(validationRunType()))
                                .argument(
                                        GraphQLArgument.newArgument()
                                                .name("id")
                                                .description("ID of the validation run to look for")
                                                .type(new GraphQLNonNull(GraphQLInt))
                                                .build()
                                )
                                .dataFetcher(validationRunFetcher())
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

    private DataFetcher validationRunFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            if (id != null) {
                // Fetch by ID
                return Collections.singletonList(
                        structureService.getValidationRun(ID.of(id))
                );
            }
            // TODO Other criterias
            // Empty list
            else {
                return Collections.emptyList();
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

    private DataFetcher validationStampValidationRunsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof ValidationStamp) {
                ValidationStamp validationStamp = (ValidationStamp) source;
                // Gets all the validation runs
                // TODO Use environment for limits?
                List<ValidationRun> validationRuns = structureService.getValidationRunsForValidationStamp(
                        validationStamp.getId(),
                        0,
                        Integer.MAX_VALUE
                );
                // As a connection list
                return new SimpleListConnection(validationRuns).get(environment);
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

    private DataFetcher branchValidationStampsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Branch) {
                Branch branch = (Branch) source;
                return structureService.getValidationStampListForBranch(branch.getId());
            } else {
                return Collections.emptyList();
            }
        };
    }

}
