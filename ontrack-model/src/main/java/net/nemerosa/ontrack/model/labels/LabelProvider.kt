package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.structure.Project

/**
 * Defines a service which can computes automatic [labels][Label] for
 * projects.
 */
interface LabelProvider {
    /**
     * Display name for the provider
     */
    val name: String

    /**
     * Gets the list of labels for a project
     */
    fun getLabelsForProject(project: Project): List<LabelForm>
}

/**
 * Gets the [LabelProviderDescription] for a [LabelProvider].
 */
val LabelProvider.description: LabelProviderDescription
    get() = LabelProviderDescription(
            this::class.java.name,
            name
    )
