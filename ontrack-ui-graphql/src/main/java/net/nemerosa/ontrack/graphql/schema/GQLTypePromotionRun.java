package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PromotionRun;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypePromotionRun extends AbstractGQLProjectEntity<PromotionRun> {

    public static final String PROMOTION_RUN = "PromotionRun";

    private final GQLProjectEntityInterface projectEntityInterface;

    @Autowired
    public GQLTypePromotionRun(
            GQLTypeCreation creation,
            List<GQLProjectEntityFieldContributor> projectEntityFieldContributors,
            GQLProjectEntityInterface projectEntityInterface,
            List<FreeTextAnnotatorContributor> freeTextAnnotatorContributors
    ) {
        super(PromotionRun.class, ProjectEntityType.PROMOTION_RUN, projectEntityFieldContributors, creation, freeTextAnnotatorContributors);
        this.projectEntityInterface = projectEntityInterface;
    }

    @Override
    public String getTypeName() {
        return PROMOTION_RUN;
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return newObject()
                .name(PROMOTION_RUN)
                .withInterface(projectEntityInterface.getTypeRef())
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

    @Nullable
    @Override
    protected Signature getSignature(@NotNull PromotionRun entity) {
        return entity.getSignature();
    }
}
