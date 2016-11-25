package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeValidationRunStatus extends AbstractGQLType {

    public static final String VALIDATION_RUN_STATUS = "ValidationRunStatus";

    private final GQLTypeValidationRunStatusID validationRunStatusID;

    @Autowired
    public GQLTypeValidationRunStatus(URIBuilder uriBuilder, SecurityService securityService, GQLTypeValidationRunStatusID validationRunStatusID) {
        super(uriBuilder, securityService);
        this.validationRunStatusID = validationRunStatusID;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(VALIDATION_RUN_STATUS)
                // TODO Signature
                // Status ID
                .field(
                        newFieldDefinition()
                                .name("statusID")
                                .description("Status ID")
                                .type(validationRunStatusID.getType())
                                .build()
                )
                // Description
                .field(GraphqlUtils.descriptionField())
                // OK
                .build();

    }

}
