package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.settings.SettingsQueryProvider
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.settings.SettingsManager
import org.springframework.stereotype.Component

@Component
class GQLTypeSettings(
    private val settingsQueryProviders: List<SettingsQueryProvider<*>>,
    private val gqlTypeSettingsEntry: GQLTypeSettingsEntry,
    private val settingsManagers: List<SettingsManager<*>>,
) : GQLType {

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
            // Getting a settings entry by ID
            .field {
                it.name("settingsById")
                    .description("Getting some settings using their ID")
                    .type(gqlTypeSettingsEntry.typeRef)
                    .argument(stringArgument("id", "ID of the settings to get", nullable = false))
                    .dataFetcher { env ->
                        val id: String = env.getArgument("id")!!
                        settingsManagers.find { settingsManager ->
                            settingsManager.id == id
                        }
                            ?.let { settingsManager ->
                                gqlTypeSettingsEntry.createEntry(settingsManager)
                            }
                    }
            }
            // List of all settings (extension, id & values)
            .field {
                it.name("list")
                    .description("List of all settings and their values")
                    .type(listType(gqlTypeSettingsEntry.typeRef))
                    .dataFetcher {
                        settingsManagers.map { settingsManager ->
                            gqlTypeSettingsEntry.createEntry(settingsManager)
                        }.sortedBy { entry ->
                            entry.title
                        }
                    }
            }
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