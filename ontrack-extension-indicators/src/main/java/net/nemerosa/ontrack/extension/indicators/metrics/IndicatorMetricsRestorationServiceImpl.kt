package net.nemerosa.ontrack.extension.indicators.metrics

import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorMetricsRestorationServiceImpl(
        private val structureService: StructureService,
        private val indicatorService: IndicatorService,
        private val indicatorMetricsService: IndicatorMetricsService
) : IndicatorMetricsRestorationService {

    override fun restore(logger: (String) -> Unit) {
        structureService.projectList.forEach {
            restore(it, logger)
        }
    }

    private fun restore(project: Project, logger: (String) -> Unit) {
        logger("Restoring indicators for project ${project.name}")
        // Gets all the indicators (past included) of this project
        indicatorService.getAllProjectIndicators(project).forEach { indicator ->
            indicatorMetricsService.saveMetrics(project, indicator)
        }
    }

}