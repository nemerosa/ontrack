package net.nemerosa.ontrack.extension.git.property

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.graphql.schema.PropertyMutationProvider
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GitCommitPropertyMutationProvider : PropertyMutationProvider<GitCommitProperty> {

    override val propertyType: KClass<out PropertyType<GitCommitProperty>> = GitCommitPropertyType::class
    override val mutationNameFragment: String = "GitCommit"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        GraphQLInputObjectField.newInputObjectField()
            .name(INPUT_COMMIT)
            .description("Full commit hash")
            .type(GraphQLNonNull(GraphQLString))
            .build()
    )

    override fun readInput(input: MutationInput) = GitCommitProperty(
        commit = input.getRequiredInput(INPUT_COMMIT)
    )

    companion object {
        private const val INPUT_COMMIT = "commit"
    }
}