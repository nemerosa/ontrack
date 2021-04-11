package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.settings.SettingsQueryProvider
import org.springframework.stereotype.Component

/**
 * GraphQL query to get the settings
 */
@Component
class GQLRootQuerySettings(
    private val gqlTypeSettings: GQLTypeSettings
): GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("settings")
            .description("Ontrack settings")
            .dataFetcher { RootSettings() } // Must not return null, but content is not important
            .type(GraphQLNonNull(gqlTypeSettings.typeRef))
            // OK
            .build()

    internal class RootSettings
}

@Component
class GQLTypeSettings(
    private val settingsQueryProviders: List<SettingsQueryProvider<*>>
): GQLType {

    override fun getTypeName(): String = "Settings"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Ontrack settings")
                // One field per settings query provider
            .fields(
                settingsQueryProviders.map {
                    createSettingsField(it)
                }
            )
                // OK
            .build()

    private fun <T> createSettingsField(settingsQueryProvider: SettingsQueryProvider<T>): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name(settingsQueryProvider.id)
            .description(settingsQueryProvider.description)
            .type(GraphQLNonNull(settingsQueryProvider.createType()))
            .dataFetcher { settingsQueryProvider.getSettings() }
            .build()

}