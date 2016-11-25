package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.model.structure.ValidationRun;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeValidationRun extends AbstractGQLProjectEntityWithSignature<ValidationRun> {

    public static final String VALIDATION_RUN = "ValidationRun";

    private final GQLTypeValidationRunStatus validationRunStatus;

    public GQLTypeValidationRun(URIBuilder uriBuilder, SecurityService securityService, List<ResourceDecorator<?>> decorators, GQLTypeValidationRunStatus validationRunStatus) {
        super(uriBuilder, securityService, ValidationRun.class, decorators);
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
