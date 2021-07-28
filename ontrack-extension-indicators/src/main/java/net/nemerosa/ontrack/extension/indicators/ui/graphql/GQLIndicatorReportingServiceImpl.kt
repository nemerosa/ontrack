package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingFilter
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GQLIndicatorReportingServiceImpl(
    private val indicatorReportingService: IndicatorReportingService
) : GQLIndicatorReportingService {

    companion object {
        const val ARG_FILLED_ONLY = "filledOnly"
        const val ARG_PROJECT_ID = "projectId"
        const val ARG_PROJECT_NAME = "projectName"
        const val ARG_PORTFOLIO = "portfolio"
        const val ARG_LABEL = "label"
    }

    override val arguments: List<GraphQLArgument> = listOf(
        GraphQLArgument.newArgument()
            .name(ARG_FILLED_ONLY)
            .description("Reports only projects where the indicator is filled in")
            .type(Scalars.GraphQLBoolean)
            .build(),
        GraphQLArgument.newArgument()
            .name(ARG_PROJECT_ID)
            .description("Reports on the project matching this ID")
            .type(Scalars.GraphQLInt)
            .build(),
        GraphQLArgument.newArgument()
            .name(ARG_PROJECT_NAME)
            .description("Reports on the project matching this name")
            .type(Scalars.GraphQLString)
            .build(),
        GraphQLArgument.newArgument()
            .name(ARG_PORTFOLIO)
            .description("Reports on the projects belonging to this portfolio")
            .type(Scalars.GraphQLString)
            .build(),
        GraphQLArgument.newArgument()
            .name(ARG_LABEL)
            .description("Reports on the projects matching this label")
            .type(Scalars.GraphQLString)
            .build()
    )

    override fun findProjects(env: DataFetchingEnvironment, types: List<IndicatorType<*, *>>): List<Project> {
        val filledOnly: Boolean? = env.getArgument(ARG_FILLED_ONLY)
        val projectId: Int? = env.getArgument(ARG_PROJECT_ID)
        val projectName: String? = env.getArgument(ARG_PROJECT_NAME)
        val portfolio: String? = env.getArgument(ARG_PORTFOLIO)
        val label: String? = env.getArgument(ARG_LABEL)

        val filter = IndicatorReportingFilter(
            filledOnly, projectName, projectId, portfolio, label
        )

        return indicatorReportingService.findProjects(filter, types)
    }
}