package net.nemerosa.ontrack.graphql.schema.actions

class UIAction<T>(
        val name: String,
        val description: String,
        val links: List<UIActionLink<T>>,
        val mutation: UIActionMutation<T>?
)
