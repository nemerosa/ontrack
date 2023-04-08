package net.nemerosa.ontrack.extension.queue.record

import com.fasterxml.jackson.databind.JsonNode
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLType
import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class QueueRecordingsExtension(
        queueExtensionFeature: QueueExtensionFeature
) : AbstractExtension(queueExtensionFeature), RecordingsExtension<QueueRecord> {

    override val id: String = "queue-recordings"

    override val displayName: String = "Queue messages"

    override fun toJson(recording: QueueRecord): JsonNode = recording.asJson()

    override fun fromJson(data: JsonNode): QueueRecord = data.parse()

    override fun graphQLRecordFields(cache: GQLTypeCache): List<GraphQLFieldDefinition> =
            GraphQLBeanConverter.asObjectFields(QueueRecord::class, GQLTypeCache())

    override fun graphQLRecordFilterFields(dictionary: MutableSet<GraphQLType>): List<GraphQLInputObjectField> =
            GraphQLBeanConverter.asInputFields(QueueRecordQueryFilter::class, dictionary)
}