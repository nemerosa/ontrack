package net.nemerosa.ontrack.graphql.schema.settings

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import kotlin.reflect.KClass

abstract class AbstractSettingsQueryProvider<T: Any>(
    private val cachedSettingsService: CachedSettingsService,
    private val settingsClass: KClass<T>
) : SettingsQueryProvider<T> {

    override fun getSettings(): T =
        cachedSettingsService.getCachedSettings(settingsClass.java)

    override fun createType(): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(id.capitalize() + "SettingsType")
            .description(description)
            .fields(fields())
            .build()

    abstract fun fields(): List<GraphQLFieldDefinition>

}