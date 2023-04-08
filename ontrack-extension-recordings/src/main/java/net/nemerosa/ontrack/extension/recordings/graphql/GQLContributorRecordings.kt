package net.nemerosa.ontrack.extension.recordings.graphql

import graphql.schema.*
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.recordings.Recording
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.graphql.schema.GQLContributor
import net.nemerosa.ontrack.graphql.schema.GQLRootQueries
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.typedArgument
import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Component

/**
 * Contribution to the GraphQL schema by the recordings extensions.
 */
@Component
class GQLContributorRecordings(
        private val extensionManager: ExtensionManager,
        private val gqlPaginatedListFactory: GQLPaginatedListFactory,
) : GQLContributor, GQLRootQueries {

    override fun contribute(cache: GQLTypeCache, dictionary: MutableSet<GraphQLType>): Set<GraphQLType> {
        val set = mutableSetOf<GraphQLType>()
        extensionManager.getExtensions(RecordingsExtension::class.java).forEach { extension ->
            set += extension.contribute(cache, dictionary)
        }
        return set
    }

    override val fieldDefinitions: List<GraphQLFieldDefinition>
        get() = extensionManager.getExtensions(RecordingsExtension::class.java).map { extension ->
            extension.createRootQuery()
        }

    private fun <R : Recording> RecordingsExtension<R>.createRootQuery(): GraphQLFieldDefinition =
            gqlPaginatedListFactory.createPaginatedField<Any?, R>(
                    cache = GQLTypeCache(),
                    fieldName = "${prefix}Recordings",
                    fieldDescription = "List of recordings for $lowerDisplayName",
                    itemType = "${prefix}Recording",
                    itemPaginatedListProvider = { env, _, offset, size ->
                        getPaginatedList(env, offset, size)
                    },
                    arguments = listOf(
                            typedArgument(ARG_FILTER, "${prefix}RecordingFilterInput", "Filter", nullable = true)
                    )
            )

    private fun <R : Recording> RecordingsExtension<R>.getPaginatedList(
            environment: DataFetchingEnvironment,
            offset: Int,
            size: Int
    ): PaginatedList<R> {
        TODO()
    }

    private fun <R : Recording> RecordingsExtension<R>.contribute(cache: GQLTypeCache, dictionary: MutableSet<GraphQLType>): Set<GraphQLType> =
            graphQLContributions + setOf(
                    // Record
                    createRecordType(cache),
                    // Filter input
                    createFilterInput(dictionary),
            )

    private fun <R : Recording> RecordingsExtension<R>.createFilterInput(dictionary: MutableSet<GraphQLType>): GraphQLInputObjectType =
            GraphQLInputObjectType.newInputObject()
                    .name("${prefix}RecordingFilterInput")
                    .description("Recording filter input for $lowerDisplayName")
                    .fields(graphQLRecordFilterFields(dictionary))
                    .build()

    private fun <R : Recording> RecordingsExtension<R>.createRecordType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name("${prefix}Recording")
                    .description("Recording for $lowerDisplayName")
                    // Common fields
                    .fields(GraphQLBeanConverter.asObjectFields(Recording::class, cache))
                    // Specific fields
                    .fields(graphQLRecordFields(cache))
                    // Ok
                    .build()

    private val <R : Recording> RecordingsExtension<R>.prefix: String get() = id.replaceFirstChar { it.titlecase() }

    private val <R : Recording> RecordingsExtension<R>.lowerDisplayName: String get() = id.replaceFirstChar { it.lowercase() }

    companion object {
        private const val ARG_FILTER = "filter"
    }

}