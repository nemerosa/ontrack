package net.nemerosa.ontrack.extension.av.project

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoVersioningProjectPropertyMutationProvider : PropertyMutationProvider<AutoVersioningProjectProperty> {

    override val propertyType: KClass<out PropertyType<AutoVersioningProjectProperty>> =
        AutoVersioningProjectPropertyType::class

    override val mutationNameFragment: String = "AutoVersioningProject"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        stringListInputField(AutoVersioningProjectProperty::branchIncludes),
        stringListInputField(AutoVersioningProjectProperty::branchExcludes),
        dateTimeInputField(AutoVersioningProjectProperty::lastActivityDate),
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput): AutoVersioningProjectProperty {
        return AutoVersioningProjectProperty(
            branchIncludes = input.getStringList(AutoVersioningProjectProperty::branchIncludes),
            branchExcludes = input.getStringList(AutoVersioningProjectProperty::branchExcludes),
            lastActivityDate = input.getDateTime(AutoVersioningProjectProperty::lastActivityDate),
        )
    }
}
