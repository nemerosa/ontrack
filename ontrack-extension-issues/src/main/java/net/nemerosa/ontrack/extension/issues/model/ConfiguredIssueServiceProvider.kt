package net.nemerosa.ontrack.extension.issues.model

import net.nemerosa.ontrack.model.structure.Project

/**
 * Defines a service which can provide, given a [Project],
 * its associated [ConfiguredIssueService] if any.
 */
interface ConfiguredIssueServiceProvider {

    /**
     * Gets any associated [ConfiguredIssueService] associated with the [project].
     *
     * @param project Project to get the configured issue service from
     * @return [ConfiguredIssueService] or `null` if none is defined
     */
    fun getConfiguredIssueService(project: Project): ConfiguredIssueService?

}