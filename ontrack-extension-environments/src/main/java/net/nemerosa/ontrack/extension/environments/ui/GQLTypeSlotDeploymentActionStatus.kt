package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotDeploymentActionStatus
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotDeploymentActionStatus : GQLType {

    override fun getTypeName(): String = SlotDeploymentActionStatus::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Result for the check of an action on a deployment")
            .booleanField(SlotDeploymentActionStatus::ok)
            .stringField(SlotDeploymentActionStatus::message)
            .build()
}