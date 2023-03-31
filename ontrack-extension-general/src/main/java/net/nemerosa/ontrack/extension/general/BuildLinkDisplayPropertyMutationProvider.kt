package net.nemerosa.ontrack.extension.general

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.graphql.schema.PropertyMutationProvider
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class BuildLinkDisplayPropertyMutationProvider : PropertyMutationProvider<BuildLinkDisplayProperty> {

    override val propertyType: KClass<out PropertyType<BuildLinkDisplayProperty>> = BuildLinkDisplayPropertyType::class

    override val mutationNameFragment: String = "BuildLinkDisplay"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        GraphQLInputObjectField.newInputObjectField()
            .name(BuildLinkDisplayProperty::useLabel.name)
            .description(
                "Configuration at project label to specify that a build link decoration " +
                        "should use the release/label of a build when available. By default, " +
                        "it displays the build name."
            )
            .type(GraphQLNonNull(GraphQLBoolean))
            .build()
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = BuildLinkDisplayProperty(
        useLabel = input.getRequiredInput(BuildLinkDisplayProperty::useLabel.name),
    )
}