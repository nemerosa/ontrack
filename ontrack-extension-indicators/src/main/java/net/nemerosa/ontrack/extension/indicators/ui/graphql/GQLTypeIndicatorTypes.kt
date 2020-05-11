package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.ui.IndicatorTypeController
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

/**
 * List of indicator types
 */
@Component
class GQLTypeIndicatorTypes(
        private val indicatorType: GQLTypeProjectIndicatorType,
        private val indicatorTypeService: IndicatorTypeService,
        private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Aggregator of indicator types")
                    .field {
                        it.name("types")
                                .description("List of indicator types")
                                .type(stdList(indicatorType.typeRef))
                                .argument {
                                    it.name(ARG_ID)
                                            .description("ID of the indicator type")
                                            .type(Scalars.GraphQLString)
                                }
                                .dataFetcher { env ->
                                    val id: String? = env.getArgument(ARG_ID)
                                    if (id != null) {
                                        val type = ProjectIndicatorType(indicatorTypeService.getTypeById(id))
                                        listOf(type)
                                    } else {
                                        indicatorTypeService.findAll().map {
                                            ProjectIndicatorType(it)
                                        }
                                    }
                                }
                    }
                    // Links
                    .fields(IndicatorTypes::class.java.graphQLFieldContributions(fieldContributors))
                    //OK
                    .build()

    override fun getTypeName(): String = "IndicatorTypes"

    companion object {
        private const val ARG_ID = "id"
    }

}

class IndicatorTypes

@Component
class IndicatorTypesResourceDecorator : AbstractLinkResourceDecorator<IndicatorTypes>(IndicatorTypes::class.java) {
    override fun getLinkDefinitions(): Iterable<LinkDefinition<IndicatorTypes>> = listOf(

            Link.CREATE linkTo { _: IndicatorTypes ->
                on(IndicatorTypeController::class.java).getCreationForm()
            } linkIfGlobal IndicatorTypeManagement::class

    )

}