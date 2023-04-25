package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.databind.JsonNode
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoVersioningRecordingsExtension(
        extensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(extensionFeature), RecordingsExtension<AutoVersioningAuditStoreData, AutoVersioningAuditQueryFilter> {

    override val id: String = "auto-versioning"

    override val graphQLPrefix: String = "AutoVersioning"

    override val displayName: String = "Auto versioning events"

    override fun graphQLRecordFields(cache: GQLTypeCache): List<GraphQLFieldDefinition> =
            GraphQLBeanConverter.asObjectFields(AutoVersioningAuditStoreData::class, cache)

    override val filterType: KClass<AutoVersioningAuditQueryFilter> = AutoVersioningAuditQueryFilter::class

    override fun fromJson(data: JsonNode): AutoVersioningAuditStoreData =
            data.parse()

    override fun filterQuery(filter: AutoVersioningAuditQueryFilter, queryVariables: MutableMap<String, Any?>): List<String> {

        val jsonQueries = mutableListOf<String>()

        // Filter on uuid
        filter.uuid?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "data::jsonb->>'uuid'"
            queryVariables += "uuid" to it
        }
        // Filter on state
        filter.state?.let {
            jsonQueries += "data::jsonb->>'mostRecentState' = :state"
            queryVariables += "state" to it.name
        }
        // Filter on state(s)
        if (!filter.states.isNullOrEmpty()) {
            val states = filter.states.joinToString(", ") { "'$it'" }
            jsonQueries += "data::jsonb->>'mostRecentState' IN ($states)"
        }
        // Filter on running state
        filter.running?.let { flag ->
            jsonQueries += "data::jsonb->>'running' = :running"
            queryVariables += "running" to flag.toString()
        }
        // Filter on source
        filter.source?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "data::jsonb->>'sourceProject' = :source"
            queryVariables += "source" to it
        }
        // Filter on version
        filter.version?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "data::jsonb->>'targetVersion' = :version"
            queryVariables += "version" to it
        }
        // Filter on routing
        filter.routing?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "data::jsonb->>'routing' = :routing"
            queryVariables += "routing" to it
        }
        // Filter on queue
        filter.queue?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "data::jsonb->>'queue' = :queue"
            queryVariables += "queue" to it
        }
        // Target project filter
        if (!filter.project.isNullOrBlank()) {
            jsonQueries += "data::jsonb->>'branchProjectName' = :branchProjectName"
            queryVariables += "branchProjectName" to filter.project
            // Target branch filter
            if (!filter.branch.isNullOrBlank()) {
                jsonQueries += "data::jsonb->>'branchName' = :branchName"
                queryVariables += "branchName" to filter.branch
            }
        }

        // OK
        return jsonQueries
    }

    override fun toJson(recording: AutoVersioningAuditStoreData): JsonNode = recording.asJson()
}