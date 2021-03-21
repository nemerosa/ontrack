package net.nemerosa.ontrack.extension.general

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.graphql.schema.PropertyMutationProvider
import net.nemerosa.ontrack.graphql.schema.optionalStringInputField
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PropertyType
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoPromotionPropertyMutationProvider(
    private val structureService: StructureService,
) : PropertyMutationProvider<AutoPromotionProperty> {

    override val propertyType: KClass<out PropertyType<AutoPromotionProperty>> = AutoPromotionPropertyType::class

    override val mutationNameFragment: String = "AutoPromotion"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        GraphQLInputObjectField.newInputObjectField()
            .name(AutoPromotionProperty::validationStamps.name)
            .description("List of needed validation stamps")
            .type(GraphQLList(GraphQLNonNull(GraphQLString)))
            .build(),
        optionalStringInputField(AutoPromotionProperty::include.name,
            "Regular expression to include validation stamps by name"),
        optionalStringInputField(AutoPromotionProperty::exclude.name,
            "Regular expression to exclude validation stamps by name"),
        GraphQLInputObjectField.newInputObjectField()
            .name(AutoPromotionProperty::promotionLevels.name)
            .description("List of needed promotion levels")
            .type(GraphQLList(GraphQLNonNull(GraphQLString)))
            .build(),
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput): AutoPromotionProperty {
        if (entity is PromotionLevel) {
            val validationStamps = input.getInput<List<String>>(AutoPromotionProperty::validationStamps.name)
                ?.mapNotNull {
                    structureService.findValidationStampByName(entity.project.name, entity.branch.name, it).getOrNull()
                }
                ?: emptyList()
            val promotionLevels = input.getInput<List<String>>(AutoPromotionProperty::promotionLevels.name)
                ?.mapNotNull {
                    structureService.findPromotionLevelByName(entity.project.name, entity.branch.name, it).getOrNull()
                }
                ?: emptyList()
            return AutoPromotionProperty(
                validationStamps = validationStamps,
                include = input.getInput<String>(AutoPromotionProperty::include.name) ?: "",
                exclude = input.getInput<String>(AutoPromotionProperty::exclude.name) ?: "",
                promotionLevels = promotionLevels,
            )
        } else {
            throw IllegalStateException("Parent entity must be a promotion level")
        }
    }
}
