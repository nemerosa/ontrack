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
class AutoPromotionLevelPropertyMutationProvider : PropertyMutationProvider<AutoPromotionLevelProperty> {

    override val propertyType: KClass<out PropertyType<AutoPromotionLevelProperty>> =
        AutoPromotionLevelPropertyType::class

    override val mutationNameFragment: String = "AutoPromotionLevel"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        optionalBooleanInputField(AutoPromotionLevelProperty::isAutoCreate.name,
            "If promotion levels must be created from predefined promotion levels"),
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = AutoPromotionLevelProperty(
        isAutoCreate = input.getInput<Boolean>(AutoPromotionLevelProperty::isAutoCreate.name) ?: false,
    )
}