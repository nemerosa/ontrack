package net.nemerosa.ontrack.boot.graphql.schema;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeBranch extends AbstractGQLProjectEntity<Branch> {

    public static final String BRANCH = "Branch";

    private final StructureService structureService;
    private final BuildFilterService buildFilterService;
    private final GQLTypeBuild build;
    private final GQLTypePromotionLevel promotionLevel;
    private final GQLTypeValidationStamp validationStamp;
    private final GQLInputBuildStandardFilter inputBuildStandardFilter;

    @Autowired
    public GQLTypeBranch(StructureService structureService,
                         BuildFilterService buildFilterService,
                         GQLTypeBuild build,
                         GQLTypePromotionLevel promotionLevel,
                         GQLTypeValidationStamp validationStamp,
                         GQLInputBuildStandardFilter inputBuildStandardFilter,
                         List<GQLProjectEntityFieldContributor> projectEntityFieldContributors) {
        super(Branch.class, ProjectEntityType.BRANCH, projectEntityFieldContributors);
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
                // TODO Ref to project
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
                                // Last promotion filter
                                .argument(
                                        newArgument()
                                                .name("lastPromotions")
                                                .description("Filter which returns the last promoted builds")
                                                .type(GraphQLBoolean)
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
                boolean lastPromotions = GraphqlUtils.getBooleanArgument(environment, "lastPromotions", false);
                // Filter to use
                BuildFilterProviderData<?> buildFilter;
                // Last promotion filter
                if (lastPromotions) {
                    buildFilter = buildFilterService.lastPromotedBuildsFilterData();
                }
                // Default filter
                else if (filter == null) {
                    buildFilter = buildFilterService.standardFilterProviderData(count).build();
                } else {
                    if (!(filter instanceof Map)) {
                        throw new IllegalStateException("Filter is expected to be a map");
                    } else {
                        @SuppressWarnings("unchecked")
                        Map<String, ?> map = (Map<String, ?>) filter;
                        JsonNode node = JsonUtils.fromMap(map);
                        buildFilter = buildFilterService.standardFilterProviderData(node);
                    }
                }
                // Result
                List<Build> builds = buildFilter.filterBranchBuilds(branch);
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
    protected Optional<Signature> getSignature(Branch entity) {
        return Optional.ofNullable(entity.getSignature());
    }

}
