package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension

/**
 * This extension registers contributions to the build search in a project.
 */
interface BuildSearchExtension : Extension {

    /**
     * Unique ID for the extension
     */
    val id: String

    /**
     * Given a search token [value], contributes to the search query.
     *
     * @param value Search token
     * @param tables SQL containing all the tables & joins
     * @param criteria SQL containing all the query criteria
     * @param params Parameters for the query
     */
    fun contribute(
        value: String,
        tables: MutableList<String>,
        criteria: MutableList<String>,
        params: MutableMap<String, Any?>,
    )

}