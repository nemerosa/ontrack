package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLRootQueryIndicatorViews(
    private val indicatorViewService: IndicatorViewService,
    private val gqlTypeIndicatorView: GQLTypeIndicatorView
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("indicatorViews")
            .description("List of indicator views.")
            .type(stdList(gqlTypeIndicatorView.typeRef))
            .dataFetcher {
                indicatorViewService.getIndicatorViews()
            }
            .build()

}