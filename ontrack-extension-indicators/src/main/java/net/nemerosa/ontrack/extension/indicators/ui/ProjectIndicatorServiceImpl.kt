package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.ID
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
                            category = category,
                            indicators = indicators.map { indicator ->
                                ProjectIndicator(
                                        project = project,
                                        type = ProjectIndicatorType(indicator.type),
                                        value = indicator.toClientJson(),
                                        status = indicator.status,
                                        comment = indicator.comment,
                                        signature = indicator.signature
                                )
                            }
                    )
                }
        )
    }

    override fun getUpdateFormForIndicator(projectId: ID, typeId: Int): Form {
        val project = structureService.getProject(projectId)
        val indicator = indicatorService.getProjectIndicator(project, typeId)
        return indicator.getUpdateForm()
    }
}