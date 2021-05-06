package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import org.springframework.stereotype.Component

@Component
class GQLRootQueryIndicatorViewList(
    private val indicatorViewService: IndicatorViewService,
    private val gqlTypeIndicatorViewList: GQLTypeIndicatorViewList
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("indicatorViewList")
            .description("List of indicator views.")
            .type(gqlTypeIndicatorViewList.typeRef)
            .dataFetcher {
                IndicatorViewList.INSTANCE
            }
            .build()

}