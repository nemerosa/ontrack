package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extensions.environments.SlotPipeline
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.localDateTimeField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipeline : GQLType {

    override fun getTypeName(): String = SlotPipeline::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Pipeline for a slot")
            .stringField(SlotPipeline::id)
            .localDateTimeField(SlotPipeline::start)
            .localDateTimeField(SlotPipeline::end)
            .enumField(SlotPipeline::status)
            .field(SlotPipeline::slot)
            .field(SlotPipeline::build)
            .build()
}