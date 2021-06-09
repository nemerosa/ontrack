package net.nemerosa.ontrack.extension.stash.property

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class BitbucketProjectConfigurationPropertyMutationProvider(
    private val stashConfigurationService: StashConfigurationService
) : PropertyMutationProvider<StashProjectConfigurationProperty> {

    override val propertyType: KClass<out PropertyType<StashProjectConfigurationProperty>> =
        StashProjectConfigurationPropertyType::class

    override val mutationNameFragment: String = "BitbucketConfiguration"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        requiredStringInputField("configuration", "Name of the Bitbucket configuration to use"),
        requiredStringInputField("bitbucketProject", "Key of the Bitbucket project to use"),
        requiredStringInputField("bitbucketRepository", "Name of the Bitbucket repository to use"),
        optionalIntInputField(
            "indexationInterval",
            "Interval (in minutes) between each indexation of the repository by Ontrack"
        ),
        optionalStringInputField(
            "issueServiceConfigurationIdentifier",
            "Issue identifier to use, for example jira//name where name is the name of the JIRA configuration in Ontrack."
        )
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = StashProjectConfigurationProperty(
        configuration = stashConfigurationService.getConfiguration(input.getRequiredInput("configuration")),
        project = input.getRequiredInput("bitbucketProject"),
        repository = input.getRequiredInput("bitbucketRepository"),
        indexationInterval = input.getInput<Int>("indexationInterval") ?: 0,
        issueServiceConfigurationIdentifier = input.getInput("issueServiceConfigurationIdentifier"),
    )
}