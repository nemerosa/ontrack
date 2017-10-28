package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeValidationRunStatusID implements GQLType {

    public static final String VALIDATION_RUN_STATUS_ID = "ValidationRunStatusID";

    @Override
    public String getTypeName() {
        return VALIDATION_RUN_STATUS_ID;
    }

    @Override
    public GraphQLObjectType createType(GQLTypeCache cache) {
        return newObject()
                .name(VALIDATION_RUN_STATUS_ID)
                // ID
                .field(
                        newFieldDefinition()
                                .name("id")
                                .description("Status ID")
                                .type(GraphQLString)
                                .build()
                )
                // Name
                .field(
                        newFieldDefinition()
                                .name("name")
                                .description("Status display name")
                                .type(GraphQLString)
                                .build()
                )
                // Root
                .field(
                        newFieldDefinition()
                                .name("root")
                                .description("Root status?")
                                .type(GraphQLBoolean)
                                .build()
                )
                // Passed
                .field(
                        newFieldDefinition()
                                .name("passed")
                                .description("Passing status?")
                                .type(GraphQLBoolean)
                                .build()
                )
                // Following statuses
                .field(
                        newFieldDefinition()
                                .name("followingStatuses")
                                .description("List of following statuses")
                                .type(stdList(GraphQLString))
                                .build()
                )
                // OK
                .build();

    }

}
