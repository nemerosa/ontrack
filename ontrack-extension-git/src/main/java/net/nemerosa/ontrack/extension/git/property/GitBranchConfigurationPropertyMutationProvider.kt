package net.nemerosa.ontrack.extension.git.property

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.graphql.schema.PropertyMutationInput
import net.nemerosa.ontrack.graphql.schema.PropertyMutationProvider
import net.nemerosa.ontrack.model.structure.PropertyType
import net.nemerosa.ontrack.model.support.NoConfig
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GitBranchConfigurationPropertyMutationProvider(
    private val link: GitCommitPropertyCommitLink
) : PropertyMutationProvider<GitBranchConfigurationProperty> {

    override val propertyType: KClass<out PropertyType<GitBranchConfigurationProperty>> =
        GitBranchConfigurationPropertyType::class
    override val mutationNameFragment: String = "GitConfig"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        GraphQLInputObjectField.newInputObjectField()
            .name(INPUT_BRANCH)
            .description("Git branch name")
            .type(GraphQLNonNull(GraphQLString))
            .build()
    )

    override fun readInput(input: PropertyMutationInput) = GitBranchConfigurationProperty(
        branch = input.getRequiredInput(INPUT_BRANCH),
        buildCommitLink = ConfiguredBuildGitCommitLink(link, NoConfig.INSTANCE).toServiceConfiguration(),
        isOverride = false,
        buildTagInterval = 0
    )

    companion object {
        private const val INPUT_BRANCH = "gitBranch"
    }
}