package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeValidationRunStatus implements GQLType {

    public static final String VALIDATION_RUN_STATUS = "ValidationRunStatus";

    private final GQLTypeValidationRunStatusID validationRunStatusID;

    @Autowired
    public GQLTypeValidationRunStatus(GQLTypeValidationRunStatusID validationRunStatusID) {
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
