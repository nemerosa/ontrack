package net.nemerosa.ontrack.extension.general

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.graphql.schema.PropertyMutationProvider
import net.nemerosa.ontrack.graphql.schema.requiredStringInputField
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class ReleasePropertyMutationProvider : PropertyMutationProvider<ReleaseProperty> {

    override val propertyType: KClass<out PropertyType<ReleaseProperty>> = ReleasePropertyType::class
    override val mutationNameFragment: String = "Release"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        requiredStringInputField("release", "Name of the release/version tag to set")
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = ReleaseProperty(
        name = input.getRequiredInput("release")
    )

}