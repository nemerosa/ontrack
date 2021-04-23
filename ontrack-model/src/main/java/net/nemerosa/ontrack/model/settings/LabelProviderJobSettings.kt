package net.nemerosa.ontrack.model.settings

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration of the label provider job settings.
 */
@APIDescription("Configuration of the label provider job settings")
data class LabelProviderJobSettings(
        @APIDescription("Check to enable the automated collection of labels for all projects. This can generate a high level activity in the background.")
        val enabled: Boolean,
        @APIDescription("Interval (in minutes) between each label scan.")
        val interval: Int,
        @APIDescription("Check to have one distinct label collection job per project.")
        val perProject: Boolean
)
