package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.labels.findLabelByDisplay
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorReportingServiceImpl(
    private val structureService: StructureService,
    private val labelManagementService: LabelManagementService,
    private val projectLabelManagementService: ProjectLabelManagementService,
    private val portfolioService: IndicatorPortfolioService,
    private val indicatorService: IndicatorService
) : IndicatorReportingService {

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

        val list = when {
            projectId != null -> listOf(
                structureService.getProject(ID.of(projectId))
            )
            projectName != null -> listOfNotNull(
                structureService.findProjectByName(projectName).getOrNull()
            )
            label != null -> {
                val actualLabel = labelManagementService.findLabelByDisplay(label)
                if (actualLabel != null) {
                    projectLabelManagementService.getProjectsForLabel(actualLabel).map { id ->
                        structureService.getProject(id)
                    }
                } else {
                    emptyList()
                }
            }
            portfolio != null -> {
                val actualPortfolio = portfolioService.findPortfolioById(portfolio)
                if (actualPortfolio != null) {
                    portfolioService.getPortfolioProjects(actualPortfolio)
                } else {
                    emptyList()
                }
            }
            else -> structureService.projectList
        }

        return if (filledOnly != null && filledOnly) {
            list.filter { project ->
                types.any { type ->
                    indicatorService.getProjectIndicator(project, type).value != null
                }
            }
        } else {
            list
        }
    }
}