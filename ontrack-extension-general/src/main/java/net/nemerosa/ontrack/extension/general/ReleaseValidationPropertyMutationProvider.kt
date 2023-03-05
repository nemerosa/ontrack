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
class ReleaseValidationPropertyMutationProvider : PropertyMutationProvider<ReleaseValidationProperty> {

    override val propertyType: KClass<out PropertyType<ReleaseValidationProperty>> =
        ReleaseValidationPropertyType::class

    override val mutationNameFragment: String = "ReleaseValidation"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        requiredStringInputField(
            ReleaseValidationProperty::validation.name,
            "Name of the validation to create when a release/label is set on a build"
        )
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = ReleaseValidationProperty(
        validation = input.getRequiredInput(ReleaseValidationProperty::validation.name)
    )
}