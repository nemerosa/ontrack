package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BranchType;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeBranch extends AbstractGQLProjectEntityWithoutSignature<Branch> {

    public static final String BRANCH = "Branch";

    private final StructureService structureService;
    private final BuildFilterService buildFilterService;
    private final GQLTypeBuild build;
    private final GQLTypePromotionLevel promotionLevel;
    private final GQLTypeValidationStamp validationStamp;
    private final GQLInputBuildStandardFilter inputBuildStandardFilter;

    @Autowired
    public GQLTypeBranch(URIBuilder uriBuilder,
                         SecurityService securityService,
                         List<ResourceDecorator<?>> decorators,
                         StructureService structureService,
                         BuildFilterService buildFilterService,
                         GQLTypeBuild build,
                         GQLTypePromotionLevel promotionLevel,
                         GQLTypeValidationStamp validationStamp,
                         EventQueryService eventQueryService,
                         GQLInputBuildStandardFilter inputBuildStandardFilter) {
        super(uriBuilder, securityService, Branch.class, decorators, eventQueryService);
        this.structureService = structureService;
        this.buildFilterService = buildFilterService;
        this.build = build;
        this.promotionLevel = promotionLevel;
        this.validationStamp = validationStamp;
        this.inputBuildStandardFilter = inputBuildStandardFilter;
    }

    @Override
    public GraphQLObjectType getType() {
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
                // Promotion levels
                .field(
                        newFieldDefinition()
                                .name("promotionLevels")
                                .type(stdList(promotionLevel.getType()))
                                .dataFetcher(branchPromotionLevelsFetcher())
                                .build()
                )
                // Validation stamps
                .field(
                        newFieldDefinition()
                                .name("validationStamps")
                                .type(stdList(validationStamp.getType()))
                                .dataFetcher(branchValidationStampsFetcher())
                                .build()
                )
                // Builds for the branch
                .field(
                        newFieldDefinition()
                                .name("builds")
                                .type(GraphqlUtils.connectionList(build.getType()))
                                // Last builds
                                .argument(
                                        newArgument()
                                                .name("count")
                                                .description("Maximum number of builds to return")
                                                .type(GraphQLInt)
                                                .build()
                                )
                                // Standard filter
                                .argument(
                                        newArgument()
                                                .name("filter")
                                                .description("Filter based on build promotions, validations, properties, ...")
                                                .type(inputBuildStandardFilter.getInputType())
                                                .build()
                                )
                                // Query
                                .dataFetcher(branchBuildsFetcher())
                                .build()
                )
                // OK
                .build();

    }

    private DataFetcher branchBuildsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Branch) {
                Branch branch = (Branch) source;
                // Count
                int count = GraphqlUtils.getIntArgument(environment, "count").orElse(10);
                Object filter = environment.getArgument("filter");
                // TODO Last promotion filter
                // Standard filter
                BuildFilter buildFilter;
                if (filter == null) {
                    buildFilter = buildFilterService.standardFilter(count).build();
                } else {
                    if (!(filter instanceof Map)) {
                        throw new IllegalStateException("Filter is expected to be a map");
                    } else {
                        @SuppressWarnings("unchecked")
                        Map<String, ?> map = (Map<String, ?>) filter;
                        buildFilter = inputBuildStandardFilter.parseMap(branch, map);
                    }
                }
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

    @Override
    protected EventType getEventCreationType() {
        return EventFactory.NEW_BRANCH;
    }
}
