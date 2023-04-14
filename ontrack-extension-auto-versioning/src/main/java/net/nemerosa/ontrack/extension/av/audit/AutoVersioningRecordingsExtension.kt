package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.databind.JsonNode
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoVersioningRecordingsExtension(
        extensionFeature: AutoVersioningExtensionFeature,
): AbstractExtension(extensionFeature), RecordingsExtension<AutoVersioningAuditStoreData, AutoVersioningAuditQueryFilter> {

    override val id: String = "auto-versioning"

    override val graphQLPrefix: String = "AutoVersioning"

    override val displayName: String = "Auto versioning events"

    override fun graphQLRecordFields(cache: GQLTypeCache): List<GraphQLFieldDefinition> {
        TODO("Not yet implemented")
    }

    override val filterType: KClass<AutoVersioningAuditQueryFilter> = AutoVersioningAuditQueryFilter::class

    override fun fromJson(data: JsonNode): AutoVersioningAuditStoreData {
        TODO("Not yet implemented")
    }

    override fun filterQuery(filter: AutoVersioningAuditQueryFilter, queryVariables: MutableMap<String, Any?>): List<String> {
        TODO("Not yet implemented")
    }

    override fun toJson(recording: AutoVersioningAuditStoreData): JsonNode {
        TODO("Not yet implemented")
    }
}