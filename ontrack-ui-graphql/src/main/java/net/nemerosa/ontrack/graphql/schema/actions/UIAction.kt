package net.nemerosa.ontrack.graphql.schema.actions

@Deprecated("Will be removed in V5.")
class UIAction<T>(
        val name: String,
        val description: String,
        val links: List<UIActionLink<T>>,
        val mutation: UIActionMutation<T>?
)
