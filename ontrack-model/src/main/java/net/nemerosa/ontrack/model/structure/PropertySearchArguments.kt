package net.nemerosa.ontrack.model.structure

/**
 * Provides an extension for a property-based search.
 * If both [jsonContext] and [jsonCriteria] are returned as `null`,
 * no search is performed for this value (negative result).
 *
 * @property jsonContext Expression to join with to the `PROPERTIES` table in order to
 *                       constrain the JSON scope, for example
 *                       `jsonb_array_elements(pp.json->'items') as item`.
 *                       Important: in the [jsonContext] context computation, the `PROPERTIES` table
 *                       is designed using the `pp` alias.
 *
 * @property jsonCriteria Criteria to act on the [jsonContext] defined above, based
 *                         on a search token, for example:
 *                         `item->>'name' = :name and item->>'value' like :value`
 *
 * @property criteriaParams Map of parameters for the criteria, for example:
 *                          `name` -> "name" and `value` -> "%value%"
 */
data class PropertySearchArguments(
        val jsonContext: String?,
        val jsonCriteria: String?,
        val criteriaParams: Map<String, *>?
) {
    val isDefined: Boolean get() = !jsonContext.isNullOrBlank() || !jsonCriteria.isNullOrBlank()
}
