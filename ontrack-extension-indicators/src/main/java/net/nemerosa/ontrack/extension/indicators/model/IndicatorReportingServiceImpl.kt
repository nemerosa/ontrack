package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.common.getOrNull
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
    private val indicatorService: IndicatorService,
) : IndicatorReportingService {

    override fun report(filter: IndicatorReportingFilter, types: List<IndicatorType<*, *>>): IndicatorProjectReport {
        // Gets the list of projects
        val projects = findProjects(filter, types)
        // Creates the report
        return IndicatorProjectReport(
            items = projects.map { project ->
                IndicatorProjectReportItem(
                    project = project,
                    indicators = types.map { type ->
                        indicatorService.getProjectIndicator(project, type)
                    }
                )
            }
        )
    }

    override fun findProjects(filter: IndicatorReportingFilter, types: List<IndicatorType<*, *>>): List<Project> {
        val list = when {
            filter.projectId != null -> listOf(
                structureService.getProject(ID.of(filter.projectId))
            )
            filter.projectName != null -> listOfNotNull(
                structureService.findProjectByName(filter.projectName).getOrNull()
            )
            filter.label != null -> {
                val actualLabel = labelManagementService.findLabelByDisplay(filter.label)
                if (actualLabel != null) {
                    projectLabelManagementService.getProjectsForLabel(actualLabel).map { id ->
                        structureService.getProject(id)
                    }
                } else {
                    emptyList()
                }
            }
            filter.portfolio != null -> {
                val actualPortfolio = portfolioService.findPortfolioById(filter.portfolio)
                if (actualPortfolio != null) {
                    portfolioService.getPortfolioProjects(actualPortfolio)
                } else {
                    emptyList()
                }
            }
            else -> structureService.projectList
        }

        return if (filter.filledOnly != null && filter.filledOnly) {
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