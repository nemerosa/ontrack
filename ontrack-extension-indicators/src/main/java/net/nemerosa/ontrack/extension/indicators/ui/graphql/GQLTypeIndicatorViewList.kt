package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewService
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
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
                    .type(stdList(gqlTypeIndicatorView.typeRef))
                    .dataFetcher {
                        indicatorViewService.getIndicatorViews()
                    }
            }
            // Links
            .fields(IndicatorViewList::class.java.graphQLFieldContributions(fieldContributors))
            //OK
            .build()

    override fun getTypeName(): String = IndicatorViewList::class.java.simpleName
}

class IndicatorViewList private constructor() {
    companion object {
        val INSTANCE = IndicatorViewList()
    }
}