package net.nemerosa.ontrack.graphql.schema;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.structure.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypePromotionLevel extends AbstractGQLProjectEntity<PromotionLevel> {

    public static final String PROMOTION_LEVEL = "PromotionLevel";

    private final StructureService structureService;
    private final GQLTypePromotionRun promotionRun;
    private final GQLProjectEntityInterface projectEntityInterface;

    @Autowired
    public GQLTypePromotionLevel(StructureService structureService,
                                 GQLTypeCreation creation,
                                 GQLTypePromotionRun promotionRun,
                                 List<GQLProjectEntityFieldContributor> projectEntityFieldContributors,
                                 GQLProjectEntityInterface projectEntityInterface
    ) {
        super(PromotionLevel.class, ProjectEntityType.PROMOTION_LEVEL,
                projectEntityFieldContributors,
                creation);
        this.structureService = structureService;
        this.promotionRun = promotionRun;
        this.projectEntityInterface = projectEntityInterface;
    }

    @Override
    public String getTypeName() {
        return PROMOTION_LEVEL;
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return newObject()
                .name(PROMOTION_LEVEL)
                .withInterface(projectEntityInterface.getTypeRef())
                .fields(projectEntityInterfaceFields())
                // Image flag
                .field(f -> f.name("image")
                        .description("Flag to indicate if an image is associated")
                        .type(Scalars.GraphQLBoolean)
                )
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
                                .description("List of runs for this promotion")
                                .type(GraphqlUtils.stdList(promotionRun.getTypeRef()))
                                .argument(GraphqlUtils.stdListArguments())
                                .dataFetcher(promotionLevelPromotionRunsFetcher())
                                .build()
                )
                // OK
                .build();
    }

    private DataFetcher promotionLevelPromotionRunsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof PromotionLevel) {
                PromotionLevel promotionLevel = (PromotionLevel) source;
                // Gets all the promotion runs
                List<PromotionRun> promotionRuns = structureService.getPromotionRunsForPromotionLevel(promotionLevel.getId());
                // Filters according to the arguments
                return GraphqlUtils.stdListArgumentsFilter(promotionRuns, environment);
            } else {
                return Collections.emptyList();
            }
        };
    }

    @Nullable
    @Override
    protected Signature getSignature(@NotNull PromotionLevel entity) {
        return entity.getSignature();
    }

}
