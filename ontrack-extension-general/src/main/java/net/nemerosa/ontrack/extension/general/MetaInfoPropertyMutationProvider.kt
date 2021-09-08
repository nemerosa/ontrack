package net.nemerosa.ontrack.extension.general

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * GraphQL mutation for the [MetaInfoPropertyType] property.
 */
@Component
class MetaInfoPropertyMutationProvider(
    gqlInputMetaInfoPropertyItem: GQLInputMetaInfoPropertyItem,
    private val propertyService: PropertyService,
) : PropertyMutationProvider<MetaInfoProperty> {

    override val propertyType: KClass<out PropertyType<MetaInfoProperty>> = MetaInfoPropertyType::class

    override val mutationNameFragment: String = "MetaInfo"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        GraphQLInputObjectField.newInputObjectField()
            .name("items")
            .description("List of meta info")
            .type(listInputType(gqlInputMetaInfoPropertyItem.typeRef))
            .build(),
        GraphQLInputObjectField.newInputObjectField()
            .name("append")
            .description("True to add the items to the existing meta info, false to override it")
            .defaultValue("false")
            .type(GraphQLBoolean)
            .build()
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput): MetaInfoProperty {
        val append = input.getInput<Boolean>("append") ?: false
        val inputItems = input.getRequiredInput<List<Map<String,*>>>("items").map {
            it.asJson().parse<MetaInfoPropertyItem>()
        }
        val actualItems = if (append) {
            val currentItems =
                propertyService.getProperty(entity, MetaInfoPropertyType::class.java).value?.items ?: emptyList()
            val currentMap = currentItems.associateBy { it.name }
            val inputMap = inputItems.associateBy { it.name }
            // Combination
            val actualMap = currentMap + inputMap
            actualMap.values
        } else {
            inputItems
        }
        return MetaInfoProperty(actualItems.toList())
    }

}

/**
 * Input item for the meta info property.
 *
 * Maps to [MetaInfoPropertyItem]
 */
@Component
class GQLInputMetaInfoPropertyItem : GQLInputType<MetaInfoPropertyItem> {

    override fun createInputType(): GraphQLInputType = GraphQLInputObjectType.newInputObject()
        .name(NAME)
        .description("Meta information property")
        .field(stringInputField(MetaInfoPropertyItem::name))
        .field(stringInputField(MetaInfoPropertyItem::value))
        .field(stringInputField(MetaInfoPropertyItem::link))
        .field(stringInputField(MetaInfoPropertyItem::category))
        .build()

    override fun convert(argument: Any?): MetaInfoPropertyItem? =
        argument?.asJson()?.parse()

    override fun getTypeRef() = GraphQLTypeReference(NAME)

    companion object {
        const val NAME = "MetaInfoPropertyItemInput"
    }

}