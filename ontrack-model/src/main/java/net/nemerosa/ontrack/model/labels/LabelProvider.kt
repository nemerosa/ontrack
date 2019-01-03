package net.nemerosa.ontrack.model.labels

interface LabelProvider {
    /**
     * Display name for the provider
     */
    val name: String
}

val LabelProvider.description: LabelProviderDescription
    get() = LabelProviderDescription(
            this::class.java.name,
            name
    )
