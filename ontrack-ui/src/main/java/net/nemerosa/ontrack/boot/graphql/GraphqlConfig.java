package net.nemerosa.ontrack.boot.graphql;

import graphql.relay.SimpleListConnection;
import graphql.schema.*;
import net.nemerosa.ontrack.boot.graphql.relay.Relay;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
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

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired
    private List<ResourceDecorator<?>> decorators;

    @Autowired
    private URIBuilder uriBuilder;

    @Autowired
    private SecurityService securityService;

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

    @Bean
    public ResourceContext graphqlResourceContext() {
        return new DefaultResourceContext(
                uriBuilder,
                securityService
        );
    }

    private <T extends ProjectEntity> List<GraphQLFieldDefinition> projectEntityInterfaceFields(Class<T> projectEntityClass) {
        List<GraphQLFieldDefinition> definitions = new ArrayList<>(
                Arrays.asList(
                        GraphqlUtils.idField(),
                        GraphqlUtils.nameField(),
                        GraphqlUtils.descriptionField()
                )
        );
        // Links
        List<String> linkNames = decorators.stream()
                .filter(decorator -> decorator.appliesFor(projectEntityClass))
                .flatMap(decorator -> decorator.getLinkNames().stream())
                .distinct()
                .collect(Collectors.toList());
        if (linkNames != null && !linkNames.isEmpty()) {
            definitions.add(
                    newFieldDefinition()
                            .name("links")
                            .description("Links")
                            .type(
                                    newObject()
                                            .name(projectEntityClass.getSimpleName() + "Links")
                                            .description(projectEntityClass.getSimpleName() + " links")
                                            .fields(
                                                    linkNames.stream()
                                                            .map(linkName -> newFieldDefinition()
                                                                    .name(linkName)
                                                                    .type(GraphQLString)
                                                                    .build()
                                                            )
                                                            .collect(Collectors.toList())
                                            )
                                            .build()
                            )
                            .dataFetcher(projectEntityLinksFetcher(projectEntityClass))
                            .build()
            );
        }
        // OK
        return definitions;
    }

    private <T extends ProjectEntity> DataFetcher projectEntityLinksFetcher(Class<T> projectEntityClass) {
        return environment -> {
            Object source = environment.getSource();
            if (projectEntityClass.isInstance(source)) {
                for (ResourceDecorator<?> decorator : decorators) {
                    if (decorator.appliesFor(projectEntityClass)) {
                        return getLinks(decorator, source);
                    }
                }
                return Collections.emptyMap();
            } else {
                return Collections.emptyMap();
            }
        };
    }

    private <T extends ProjectEntity> Map<String, String> getLinks(ResourceDecorator<?> decorator, Object source) {
        @SuppressWarnings("unchecked")
        ResourceDecorator<T> resourceDecorator = (ResourceDecorator<T>) decorator;
        @SuppressWarnings("unchecked")
        T t = (T) source;

        return resourceDecorator.links(
                t,
                graphqlResourceContext()
        ).stream().collect(Collectors.toMap(
                Link::getName,
                link -> link.getHref().toString()
        ));
    }

    private GraphQLInterfaceType projectEntityInterface() {
        return GraphQLInterfaceType.newInterface()
                .name(PROJECT_ENTITY)
                .fields(projectEntityInterfaceFields(Project.class))
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
                .fields(projectEntityInterfaceFields(Build.class))
                // TODO Signature
                // Promotion runs
                .field(
                        newFieldDefinition()
                                .name("promotionRuns")
                                .description("Promotions for this build")
                                .argument(
                                        newArgument()
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
                .fields(projectEntityInterfaceFields(Branch.class))
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
                                        newArgument()
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
                .fields(projectEntityInterfaceFields(PromotionLevel.class))
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
                .fields(projectEntityInterfaceFields(ValidationStamp.class))
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
                .fields(projectEntityInterfaceFields(PromotionRun.class))
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
                .fields(projectEntityInterfaceFields(ValidationRun.class))
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
                .fields(projectEntityInterfaceFields(Project.class))
                .field(GraphqlUtils.disabledField())
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(stdList(branchType()))
                                .argument(
                                        newArgument()
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
                                        newArgument()
                                                .name("id")
                                                .description("ID of the project to look for")
                                                .type(GraphQLInt)
                                                .build()
                                )
                                .argument(
                                        newArgument()
                                                .name("name")
                                                .description("Name of the project to look for")
                                                .type(GraphQLString)
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
                                .argument(
                                        newArgument()
                                                .name("id")
                                                .description("ID of the branch to look for")
                                                .type(new GraphQLNonNull(GraphQLInt))
                                                .build()
                                )
                                .dataFetcher(branchFetcher())
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
                                        newArgument()
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
            String name = environment.getArgument("name");
            // Per ID
            if (id != null) {
                // TODO No other argument is expected
                // Fetch by ID
                Project project = structureService.getProject(ID.of(id));
                // As list
                return Collections.singletonList(project);
            }
            // Name
            else if (name != null) {
                // TODO No other argument is expected
                return structureService.findProjectByName(name)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
            }
            // TODO Other criterias
            // Whole list
            else {
                return structureService.getProjectList();
            }
        };
    }

    private DataFetcher branchFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            if (id != null) {
                return Collections.singletonList(
                        structureService.getBranch(ID.of(id))
                );
            }
            // Whole list
            else {
                return Collections.emptyList();
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
