package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.schema.GQLModel.PROMOTION_RUN;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLBuild extends AbstractGQLProjectEntity<Build> {

    public static final String BUILD = "Build";

    private final StructureService structureService;

    @Autowired
    public GQLBuild(URIBuilder uriBuilder,
                    SecurityService securityService,
                    List<ResourceDecorator<?>> decorators,
                    StructureService structureService) {
        super(uriBuilder, securityService, Build.class, decorators);
        this.structureService = structureService;
    }

    @Override
    public GraphQLObjectType getType() {
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
                // OK
                .build();

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

}
