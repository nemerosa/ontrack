package net.nemerosa.ontrack.extension.stale

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class StalePropertyMutationProvider : PropertyMutationProvider<StaleProperty> {

    override val propertyType: KClass<out PropertyType<StaleProperty>> = StalePropertyType::class
    override val mutationNameFragment: String = "Stale"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        intInputField(StaleProperty::disablingDuration),
        intInputField(StaleProperty::deletingDuration),
        stringListInputField(StaleProperty::promotionsToKeep),
        stringInputField(StaleProperty::includes),
        stringInputField(StaleProperty::excludes),
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = StaleProperty(
        disablingDuration = input.getRequiredInt(StaleProperty::disablingDuration),
        deletingDuration = input.getInt(StaleProperty::deletingDuration),
        promotionsToKeep = input.getStringList(StaleProperty::promotionsToKeep),
        includes = input.getString(StaleProperty::includes),
        excludes = input.getString(StaleProperty::excludes),
    )
}