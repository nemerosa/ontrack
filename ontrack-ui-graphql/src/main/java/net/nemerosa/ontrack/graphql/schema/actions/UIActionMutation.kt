package net.nemerosa.ontrack.graphql.schema.actions

/**
 * An `ActionMutation` refers to a
 * GraphQL mutation, which is enabled or
 * not according to authorizations or
 * state.
 */
class UIActionMutation<T>(
        val name: String,
        val enabled: (T) -> Boolean
)
