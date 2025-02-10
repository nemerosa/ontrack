package net.nemerosa.ontrack.graphql.schema.trigger

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.trigger.TriggerData
import org.springframework.stereotype.Component

@Component
class GQLTypeTriggerData : GQLType {

    override fun getTypeName(): String = TriggerData::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Data for the trigger of something (notification, workflow, etc.)")
            .stringField(TriggerData::id)
            .jsonField(TriggerData::data)
            .build()
}