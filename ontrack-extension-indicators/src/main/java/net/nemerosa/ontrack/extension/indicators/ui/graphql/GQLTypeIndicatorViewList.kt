package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewService
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

/**
 * Management of [IndicatorView] list.
 */
@Component
class GQLTypeIndicatorViewList(
    private val gqlTypeIndicatorView: GQLTypeIndicatorView,
    private val indicatorViewService: IndicatorViewService,
    private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of indicator views and management links.")
            .field {
                it.name("views")
                    .description("List of indicator views")
                    .type(listType(gqlTypeIndicatorView.typeRef))
                    .argument { a ->
                        a.name(ARG_ID)
                            .description("ID of the view to put in the list")
                            .type(GraphQLString)
                    }
                    .argument { a ->
                        a.name(ARG_NAME)
                            .description("Name of the view to put in the list")
                            .type(GraphQLString)
                    }
                    .dataFetcher { env ->
                        val id: String? = env.getArgument(ARG_ID)
                        val name: String? = env.getArgument(ARG_NAME)
                        if (id != null) {
                            listOfNotNull(
                                indicatorViewService.findIndicatorViewById(id)
                            )
                        } else if (name != null) {
                            listOfNotNull(
                                indicatorViewService.findIndicatorViewByName(name)
                            )
                        } else {
                            indicatorViewService.getIndicatorViews()
                        }
                    }
            }
            // Links
            .fields(IndicatorViewList::class.java.graphQLFieldContributions(fieldContributors))
            //OK
            .build()

    override fun getTypeName(): String = IndicatorViewList::class.java.simpleName

    companion object {
        const val ARG_ID = "id"
        const val ARG_NAME = "name"
    }
}

class IndicatorViewList private constructor() {
    companion object {
        val INSTANCE = IndicatorViewList()
    }
}
