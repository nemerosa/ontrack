package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extensions.environments.SlotPipelineChange
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineChange : GQLType {

    override fun getTypeName(): String = SlotPipelineChange::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Change happening to a pipeline")
            .stringField(SlotPipelineChange::id)
            .field(SlotPipelineChange::pipeline)
            .stringField(SlotPipelineChange::user)
            .localDateTimeField(SlotPipelineChange::timestamp)
            .enumField(SlotPipelineChange::status)
            .stringField(SlotPipelineChange::message)
            .booleanField(SlotPipelineChange::override)
            .stringField(SlotPipelineChange::overrideMessage)
            .build()

}