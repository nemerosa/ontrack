package net.nemerosa.ontrack.extension.github.property

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GitHubProjectConfigurationPropertyMutationProvider(
    private val gitHubConfigurationService: GitHubConfigurationService
) :
    PropertyMutationProvider<GitHubProjectConfigurationProperty> {

    override val propertyType: KClass<out PropertyType<GitHubProjectConfigurationProperty>> =
        GitHubProjectConfigurationPropertyType::class
    override val mutationNameFragment: String = "GitHubConfiguration"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        requiredStringInputField("configuration", "Name of the GitHub configuration to use"),
        requiredStringInputField("repository", "GitHub repository to use, in the form of `organization/name`"),
        optionalIntInputField("indexationInterval", "GitHub repository interval to use"),
        optionalStringInputField("issueServiceConfigurationIdentifier",
            "Issue identifier to use, for example jira//name where name is the name of the JIRA configuration in Ontrack.")
    )

    override fun readInput(input: MutationInput) = GitHubProjectConfigurationProperty(
        configuration = gitHubConfigurationService.getConfiguration(input.getRequiredInput("configuration")),
        repository = input.getRequiredInput("repository"),
        indexationInterval = input.getInput<Int>("indexationInterval") ?: 0,
        issueServiceConfigurationIdentifier = input.getInput("issueServiceConfigurationIdentifier")
    )
}