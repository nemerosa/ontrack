package net.nemerosa.ontrack.graphql.schema.events

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.annotations.getPropertyName
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import net.nemerosa.ontrack.model.support.NameValue
import org.springframework.stereotype.Component

@Component
class GQLTypeSerializableEvent : GQLType {

    override fun getTypeName(): String = SerializableEvent::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Minimal form for an event")
            .stringField(SerializableEvent::eventType)
            .field {
                it.name(getPropertyName(SerializableEvent::entities))
                    .description("List of entities")
                    .type(listType(ProjectEntityID::class.java.simpleName))
                    .dataFetcher { env ->
                        val se: SerializableEvent = env.getSource()
                        se.entities.map { ProjectEntityID(it.key, it.value) }.sortedBy { it.type }
                    }
            }
            .field {
                it.name(getPropertyName(SerializableEvent::values))
                    .description("List of arbitrary values")
                    .type(listType(NameValue::class.java.simpleName))
                    .dataFetcher { env ->
                        val se: SerializableEvent = env.getSource()
                        se.values.values.sortedBy { it.name }
                    }
            }
            .build()
}