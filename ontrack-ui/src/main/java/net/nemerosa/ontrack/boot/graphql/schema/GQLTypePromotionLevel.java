package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.boot.graphql.support.Relay;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypePromotionLevel extends AbstractGQLProjectEntity<PromotionLevel> {

    public static final String PROMOTION_LEVEL = "PromotionLevel";

    private final StructureService structureService;
    private final GQLTypePromotionRun promotionRun;

    @Autowired
    public GQLTypePromotionLevel(StructureService structureService,
                                 GQLTypePromotionRun promotionRun,
                                 List<GQLProjectEntityFieldContributor> projectEntityFieldContributors) {
        super(PromotionLevel.class, ProjectEntityType.PROMOTION_LEVEL,
                projectEntityFieldContributors
        );
        this.structureService = structureService;
        this.promotionRun = promotionRun;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(PROMOTION_LEVEL)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // Promotion runs
                .field(
                        newFieldDefinition()
                                .name("promotionRuns")
                                .description("List of runs for this promotion")
                                .type(GraphqlUtils.connectionList(promotionRun.getType()))
                                .argument(Relay.getConnectionFieldArguments())
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
                // As a connection list
                return new SimpleListConnection(promotionRuns).get(environment);
            } else {
                return Collections.emptyList();
            }
        };
    }

    @Override
    protected Optional<Signature> getSignature(PromotionLevel entity) {
        return Optional.ofNullable(entity.getSignature());
    }

}
