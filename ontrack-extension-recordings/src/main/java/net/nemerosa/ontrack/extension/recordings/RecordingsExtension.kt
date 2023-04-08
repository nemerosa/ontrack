package net.nemerosa.ontrack.extension.recordings

import com.fasterxml.jackson.databind.JsonNode
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.model.extension.Extension

/**
 * Extension to define a schema for recording messages.
 *
 * This will provide, for the client extension:
 *
 * * some storage
 * * some services (recording, querying, cleaning)
 * * some settings
 * * some GraphQL types, queries and mutations
 * * a UI user menu extension
 * * a UI directive for a page showing all the records
 */
interface RecordingsExtension<R : Recording> : Extension {

    /**
     * Unique ID for the extension (used for storage keys and other elements)
     */
    val id: String

    /**
     * Short display name
     */
    val displayName: String

    /**
     * Creates the JSON representation of the recording so that it can be stored.
     */
    fun toJson(recording: R): JsonNode

    /**
     * Additional types needed for the extension
     */
    val graphQLContributions: Set<GraphQLType> get() = emptySet()

    /**
     * Gets the list of fields to set in the record.
     *
     * Each field will get an instance of the record [R] as a source.
     */
    fun graphQLRecordFields(cache: GQLTypeCache): List<GraphQLFieldDefinition>

    /**
     * List of the input fields for the filter on records
     */
    fun graphQLRecordFilterFields(dictionary: MutableSet<GraphQLType>): List<GraphQLInputObjectField> = emptyList()

    /**
     * Given a stored JSON, parses it into the extension data.
     */
    fun fromJson(data: JsonNode): R

}
