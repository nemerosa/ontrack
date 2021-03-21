package net.nemerosa.ontrack.extension.general

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.graphql.schema.PropertyMutationProvider
import net.nemerosa.ontrack.graphql.schema.optionalBooleanInputField
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoValidationStampPropertyMutationProvider: PropertyMutationProvider<AutoValidationStampProperty> {

    override val propertyType: KClass<out PropertyType<AutoValidationStampProperty>> = AutoValidationStampPropertyType::class

    override val mutationNameFragment: String = "AutoValidationStamp"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        optionalBooleanInputField(AutoValidationStampProperty::isAutoCreate.name, "If validation stamps must be created from predefined validation stamps"),
        optionalBooleanInputField(AutoValidationStampProperty::isAutoCreateIfNotPredefined.name, "If validation stamps must be created even if predefined validation stamp is not available"),
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = AutoValidationStampProperty(
        isAutoCreate = input.getInput<Boolean>(AutoValidationStampProperty::isAutoCreate.name) ?: false,
        isAutoCreateIfNotPredefined = input.getInput<Boolean>(AutoValidationStampProperty::isAutoCreateIfNotPredefined.name) ?: false,
    )
}