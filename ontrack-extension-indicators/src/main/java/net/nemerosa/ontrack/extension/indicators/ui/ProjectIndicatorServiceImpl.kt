package net.nemerosa.ontrack.extension.indicators.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.model.Indicator
import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProjectIndicatorServiceImpl(
        private val structureService: StructureService,
        private val indicatorService: IndicatorService
) : ProjectIndicatorService {

    override fun getProjectIndicators(projectId: ID, all: Boolean): ProjectIndicators {
        val project = structureService.getProject(projectId)
        val indicators = indicatorService.getProjectIndicators(project, all)
        return ProjectIndicators(
                project = project,
                categories = indicators.groupBy(
                        keySelector = { indicator ->
                            indicator.type.category
                        }
                ).map { (category, indicators) ->
                    ProjectCategoryIndicators(
                            project = project,
                            category = category,
                            indicators = indicators.map { indicator ->
                                toProjectIndicator(project, indicator)
                            }
                    )
                }
        )
    }

    override fun getUpdateFormForIndicator(projectId: ID, typeId: String): Form {
        val project = structureService.getProject(projectId)
        val indicator = indicatorService.getProjectIndicator(project, typeId)
        return indicator.getUpdateForm()
    }

    override fun updateIndicator(projectId: ID, typeId: String, input: JsonNode): ProjectIndicator {
        val project = structureService.getProject(projectId)
        val indicator = indicatorService.updateProjectIndicator<Any>(project, typeId, input)
        return toProjectIndicator(project, indicator)
    }

    override fun deleteIndicator(projectId: ID, typeId: String): Ack {
        val project = structureService.getProject(projectId)
        return indicatorService.deleteProjectIndicator(project, typeId)
    }

    private fun toProjectIndicator(project: Project, indicator: Indicator<*>) = ProjectIndicator(
            project = project,
            type = ProjectIndicatorType(indicator.type),
            value = indicator.toClientJson(),
            compliance = indicator.compliance,
            comment = indicator.comment,
            signature = indicator.signature
    )
}