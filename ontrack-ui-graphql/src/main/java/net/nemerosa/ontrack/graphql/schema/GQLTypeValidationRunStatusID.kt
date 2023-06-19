package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

@Component
class GQLTypeValidationRunStatusID : GQLType {

    override fun getTypeName(): String = VALIDATION_RUN_STATUS_ID

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(VALIDATION_RUN_STATUS_ID)
                // ID
                .stringField(ValidationRunStatusID::id)
                // Name
                .stringField(ValidationRunStatusID::name)
                // Root
                .booleanField(ValidationRunStatusID::isRoot)
                // Passed
                .booleanField(ValidationRunStatusID::isPassed)
                // Following statuses
                .field(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("followingStatuses")
                                .description("List of following statuses")
                                .type(listType(Scalars.GraphQLString))
                                .build()
                )
                // OK
                .build()
    }

    companion object {
        const val VALIDATION_RUN_STATUS_ID = "ValidationRunStatusID"
    }
}
