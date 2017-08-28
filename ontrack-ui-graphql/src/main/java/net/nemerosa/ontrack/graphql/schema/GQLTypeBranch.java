package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeBranch extends AbstractGQLProjectEntity<Branch> {

    public static final String BRANCH = "Branch";

    private final StructureService structureService;
    private final BuildFilterService buildFilterService;
    private final GQLTypeBuild build;
    private final GQLTypePromotionLevel promotionLevel;
    private final GQLTypeValidationStamp validationStamp;
    private final GQLInputBuildStandardFilter inputBuildStandardFilter;
    private final GQLInputBuildGenericFilter inputBuildGenericFilter;

    @Autowired
    public GQLTypeBranch(StructureService structureService,
                         BuildFilterService buildFilterService,
                         GQLTypeCreation creation,
                         GQLTypeBuild build,
                         GQLTypePromotionLevel promotionLevel,
                         GQLTypeValidationStamp validationStamp,
                         GQLInputBuildStandardFilter inputBuildStandardFilter,
                         List<GQLProjectEntityFieldContributor> projectEntityFieldContributors,
                         GQLInputBuildGenericFilter inputBuildGenericFilter) {
        super(Branch.class, ProjectEntityType.BRANCH, projectEntityFieldContributors, creation);
        this.structureService = structureService;
        this.buildFilterService = buildFilterService;
        this.build = build;
        this.promotionLevel = promotionLevel;
        this.validationStamp = validationStamp;
        this.inputBuildStandardFilter = inputBuildStandardFilter;
        this.inputBuildGenericFilter = inputBuildGenericFilter;
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
                // Ref to project
                .field(
                        newFieldDefinition()
                                .name("project")
                                .description("Reference to project")
                                .type(new GraphQLTypeReference(GQLTypeProject.PROJECT))
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
                                .argument(arg -> arg.name("name")
                                        .description("Filters on the validation stamp")
                                        .type(GraphQLString)
                                )
                                .dataFetcher(branchValidationStampsFetcher())
                                .build()
                )
                // Builds for the branch
                .field(
                        newFieldDefinition()
                                .name("builds")
                                .type(GraphqlUtils.stdList(build.getType()))
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
                                // Generic filter
                                .argument(
                                        newArgument()
                                                .name("generic")
                                                .description("Generic filter based on a configured filter")
                                                .type(inputBuildGenericFilter.getInputType())
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
                Object genericFilter = environment.getArgument("generic");
                boolean lastPromotions = GraphqlUtils.getBooleanArgument(environment, "lastPromotions", false);
                // Filter to use
                BuildFilterProviderData<?> buildFilter;
                // Last promotion filter
                if (lastPromotions) {
                    buildFilter = buildFilterService.lastPromotedBuildsFilterData();
                }
                // Standard filter
                else if (filter != null) {
                    buildFilter = inputBuildStandardFilter.convert(filter);
                }
                // Generic filter
                else if (genericFilter != null) {
                    buildFilter = inputBuildGenericFilter.convert(genericFilter);
                }
                // Default filter
                else {
                    buildFilter = buildFilterService.standardFilterProviderData(count).build();
                }
                // Result
                return buildFilter.filterBranchBuilds(branch);
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
            Optional<String> name = GraphqlUtils.getStringArgument(environment, "name");
            if (source instanceof Branch) {
                Branch branch = (Branch) source;
                if (name.isPresent()) {
                    return structureService.findValidationStampByName(
                            branch.getProject().getName(), branch.getName(), name.get()
                    )
                            .map(Collections::singletonList)
                            .orElse(Collections.emptyList());
                } else {
                    return structureService.getValidationStampListForBranch(branch.getId());
                }
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
