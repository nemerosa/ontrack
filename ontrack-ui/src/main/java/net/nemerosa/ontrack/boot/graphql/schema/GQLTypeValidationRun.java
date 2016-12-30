package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.model.structure.ValidationRun;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeValidationRun extends AbstractGQLProjectEntity<ValidationRun> {

    public static final String VALIDATION_RUN = "ValidationRun";

    private final GQLTypeValidationRunStatus validationRunStatus;

    public GQLTypeValidationRun(GQLTypeValidationRunStatus validationRunStatus,
                                PropertyService propertyService,
                                GQLTypeProperty property,
                                List<GQLProjectEntityFieldContributor> projectEntityFieldContributors) {
        super(ValidationRun.class, ProjectEntityType.VALIDATION_RUN, propertyService, property, projectEntityFieldContributors);
        this.validationRunStatus = validationRunStatus;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(VALIDATION_RUN)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // Build
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
                                .name("validationStamp")
                                .description("Associated validation stamp")
                                .type(new GraphQLNonNull(new GraphQLTypeReference(GQLTypeValidationStamp.VALIDATION_STAMP)))
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
                                .type(stdList(validationRunStatus.getType()))
                                .build()
                )
                // OK
                .build();

    }

    @Override
    protected Optional<Signature> getSignature(ValidationRun entity) {
        return Optional.ofNullable(entity.getLastStatus().getSignature());
    }
}
