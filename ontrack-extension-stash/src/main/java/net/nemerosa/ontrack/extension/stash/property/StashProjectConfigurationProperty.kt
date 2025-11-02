package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.model.getRepositoryUrl
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.json.schema.JsonSchemaString
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * @property configuration Link to the Bitbucket configuration
 * @property project Project in Bitbucket
 * @property repository Repository in the project
 * @property indexationInterval Indexation interval
 * @property issueServiceConfigurationIdentifier ID to the [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 * with this repository.
 */
class StashProjectConfigurationProperty(
    @DocumentationType("String", "Name of the Bitbucket Server configuration")
    @JsonSchemaString
    override val configuration: StashConfiguration,
    @APIDescription("Name of the project")
    val project: String,
    @APIDescription("Name of the repository")
    val repository: String,
    @APIDescription("How often to index the repository, in minutes. Use 0 to disable indexation.")
    val indexationInterval: Int,
    @APIDescription("Identifier for the issue service")
    val issueServiceConfigurationIdentifier: String?
) : ConfigurationProperty<StashConfiguration> {

    /**
     * Link to the repository
     */
    @DocumentationIgnore
    val repositoryUrl: String
        get() = getRepositoryUrl(configuration, project, repository)

}
