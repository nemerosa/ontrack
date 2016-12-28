package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PromotionRun;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypePromotionRun extends AbstractGQLProjectEntityWithSignature<PromotionRun> {

    public static final String PROMOTION_RUN = "PromotionRun";

    public GQLTypePromotionRun(URIBuilder uriBuilder,
                               SecurityService securityService,
                               List<ResourceDecorator<?>> decorators,
                               PropertyService propertyService,
                               GQLTypeProperty property) {
        super(uriBuilder, securityService, PromotionRun.class, ProjectEntityType.PROMOTION_RUN, decorators, propertyService, property);
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(PROMOTION_RUN)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                .field(
                        newFieldDefinition()
                                .name("build")
                                .description("Associated build")
                                .type(new GraphQLNonNull(new GraphQLTypeReference(GQLTypeBuild.BUILD)))
                                .build()
                )
                // Promotion level
                .field(
                        newFieldDefinition()
                                .name("promotionLevel")
                                .description("Associated promotion level")
                                .type(new GraphQLNonNull(new GraphQLTypeReference(GQLTypePromotionLevel.PROMOTION_LEVEL)))
                                .build()
                )
                // OK
                .build();
    }

    @Override
    protected Optional<Signature> getSignature(PromotionRun entity) {
        return Optional.ofNullable(entity.getSignature());
    }
}
