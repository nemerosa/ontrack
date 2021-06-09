package net.nemerosa.ontrack.extension.gitlab.property

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GitLabProjectConfigurationPropertyMutationProvider(
    private val gitLabConfigurationService: GitLabConfigurationService
) :
    PropertyMutationProvider<GitLabProjectConfigurationProperty> {

    override val propertyType: KClass<out PropertyType<GitLabProjectConfigurationProperty>> =
        GitLabProjectConfigurationPropertyType::class

    override val mutationNameFragment: String = "GitLabConfiguration"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        requiredStringInputField("configuration", "Name of the GitLab configuration to use"),
        requiredStringInputField("repository", "GitLab repository to use, in the form of `project/name`"),
        optionalIntInputField(
            "indexationInterval",
            "Interval (in minutes) between each indexation of the repository by Ontrack"
        ),
        optionalStringInputField(
            "issueServiceConfigurationIdentifier",
            "Issue identifier to use, for example jira//name where name is the name of the JIRA configuration in Ontrack."
        )
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = GitLabProjectConfigurationProperty(
        configuration = gitLabConfigurationService.getConfiguration(input.getRequiredInput("configuration")),
        repository = input.getRequiredInput("repository"),
        indexationInterval = input.getInput<Int>("indexationInterval") ?: 0,
        issueServiceConfigurationIdentifier = input.getInput("issueServiceConfigurationIdentifier")
    )
}