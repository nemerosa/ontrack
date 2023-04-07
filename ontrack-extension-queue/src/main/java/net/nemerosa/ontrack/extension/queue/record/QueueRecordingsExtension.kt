package net.nemerosa.ontrack.extension.queue.record

import com.fasterxml.jackson.databind.JsonNode
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class QueueRecordingsExtension(
        queueExtensionFeature: QueueExtensionFeature
) : AbstractExtension(queueExtensionFeature), RecordingsExtension<QueueRecord> {

    override val id: String = "queue-recordings"

    override val displayName: String = "Queue messages"

    override fun toJson(recording: QueueRecord): JsonNode = recording.asJson()

    override fun graphQLRecordFields(cache: GQLTypeCache): List<GraphQLFieldDefinition> =
            GraphQLBeanConverter.asObjectFields(QueueRecord::class, GQLTypeCache())

}