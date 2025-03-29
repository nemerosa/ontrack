package net.nemerosa.ontrack.extension.chart.core

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.chart.ChartRegistry
import net.nemerosa.ontrack.extension.chart.GQLTypeChartDefinition
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.stereotype.Component

@Component
class GQLValidationStampChartsFieldContributor(
    private val gqlTypeChartDefinition: GQLTypeChartDefinition,
    private val chartRegistry: ChartRegistry,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.VALIDATION_STAMP) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("charts")
                    .description("List of charts exposed by the validation stamp")
                    .type(listType(gqlTypeChartDefinition.typeRef))
                    .dataFetcher { env ->
                        val vs: ValidationStamp = env.getSource()!!
                        val providers = chartRegistry.getProvidersForSubjectClass(ValidationStamp::class)
                        providers.mapNotNull { provider ->
                            provider.getChartDefinition(vs)
                        }.sortedBy { it.title }
                    }
                    .build()
            )
        } else {
            null
        }
}