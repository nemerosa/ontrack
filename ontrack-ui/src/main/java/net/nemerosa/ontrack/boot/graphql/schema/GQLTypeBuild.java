package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.fetcher;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeBuild extends AbstractGQLProjectEntity<Build> {

    public static final String BUILD = "Build";

    private final StructureService structureService;

    @Autowired
    public GQLTypeBuild(StructureService structureService,
                        List<GQLProjectEntityFieldContributor> projectEntityFieldContributors) {
        super(Build.class, ProjectEntityType.BUILD, projectEntityFieldContributors);
        this.structureService = structureService;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(BUILD)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // Ref to branch
                .field(
                        newFieldDefinition()
                                .name("branch")
                                .description("Reference to branch")
                                .type(new GraphQLTypeReference(GQLTypeBranch.BRANCH))
                                .build()
                )
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
                                .type(stdList(new GraphQLTypeReference(GQLTypePromotionRun.PROMOTION_RUN)))
                                .dataFetcher(buildPromotionRunsFetcher())
                                .build()
                )
                // Validation runs
                .field(
                        newFieldDefinition()
                                .name("validationRuns")
                                .description("Validations for this build")
                                .argument(
                                        newArgument()
                                                .name("validationStamp")
                                                .description("Name of the validation stamp")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .argument(
                                        newArgument()
                                                .name("count")
                                                .description("Maximum number of validation runs")
                                                .type(GraphQLInt)
                                                .defaultValue(50)
                                                .build()
                                )
                                .type(stdList(new GraphQLTypeReference(GQLTypeValidationRun.VALIDATION_RUN)))
                                .dataFetcher(buildValidationRunsFetcher())
                                .build()
                )
                // Build links
                .field(
                        newFieldDefinition()
                                .name("linkedBuilds")
                                .description("Builds this build is linked to")
                                .type(stdList(new GraphQLTypeReference(BUILD)))
                                .dataFetcher(buildLinkedToFetcher())
                                .build()
                )
                // OK
                .build();
    }

    private DataFetcher buildLinkedToFetcher() {
        return fetcher(
                Build.class,
                structureService::getBuildLinksFrom
        );
    }

    private DataFetcher buildValidationRunsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Build) {
                Build build = (Build) source;
                // Filter
                int count = GraphqlUtils.getIntArgument(environment, "count").orElse(50);
                String validation = GraphqlUtils.getStringArgument(environment, "validation").orElse(null);
                if (validation != null) {
                    // Gets the validation stamp
                    ValidationStamp validationStamp = structureService.findValidationStampByName(
                            build.getProject().getName(),
                            build.getBranch().getName(),
                            validation
                    ).orElseThrow(() -> new ValidationStampNotFoundException(
                            build.getProject().getName(),
                            build.getBranch().getName(),
                            validation
                    ));
                    // Gets validations runs for this validation level
                    return structureService.getValidationRunsForBuildAndValidationStamp(
                            build.getId(),
                            validationStamp.getId()
                    )
                            .stream()
                            .limit(count)
                            .collect(Collectors.toList());
                } else {
                    // Gets all the validation runs (limited by count)
                    return structureService.getValidationRunsForBuild(build.getId()).stream()
                            .limit(count)
                            .collect(Collectors.toList());
                }
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

    @Override
    protected Optional<Signature> getSignature(Build entity) {
        return Optional.ofNullable(entity.getSignature());
    }
}
