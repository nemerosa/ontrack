package net.nemerosa.ontrack.graphql.schema.authorizations

/**
 * Name associated with a check.
 *
 * @param T Type to check
 * @param name Name of the resulting authorization
 * @param description Description of the resulting authorization
 * @param check Check to perform
 */
class Authorization<T>(
        val name: String,
        val description: String,
        val check: (T) -> Boolean
)
