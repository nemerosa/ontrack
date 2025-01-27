package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.PropertyType

/**
 * The [CoreBuildFilterRepository] uses this interface in order to complete
 * the building of the search query or resolution.
 */
interface CoreBuildFilterRepositoryHelper {

    /**
     * Given the full name of a property, returns the associated property type.
     */
    fun propertyTypeAccessor(type: String): PropertyType<*>

    /**
     * Given a build search extension ID, contributes to the search query.
     *
     * @param extension Extension ID
     * @param value Search token
     * @param tables SQL containing all the tables & joins
     * @param criteria SQL containing all the query criteria
     * @param params Parameters for the query
     */
    fun contribute(
        extension: String,
        value: String,
        tables: MutableList<String>,
        criteria: MutableList<String>,
        params: MutableMap<String, Any?>,
    )
}