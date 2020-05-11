package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import org.springframework.stereotype.Service

@Service
class IndicatorStatsServiceImpl(
        private val indicatorPortfolioService: IndicatorPortfolioService,
        private val indicatorTypeService: IndicatorTypeService,
        private val indicatorService: IndicatorService
) : IndicatorStatsService {

    override fun getGlobalStats(portfolio: IndicatorPortfolio): List<IndicatorTypeStats> {
        // Gets the global types
        val types = indicatorPortfolioService.getPortfolioOfPortfolios().types.mapNotNull {
            indicatorTypeService.findTypeById(it)
        }
        // Gets the projects for this portfolio
        val projects = indicatorPortfolioService.getPortfolioProjects(portfolio)
        // For each type
        return types.map { type ->
            val statuses = projects.map { project ->
                indicatorService.getProjectIndicator(project, type).compliance
            }
            IndicatorTypeStats(
                    type = type,
                    stats = IndicatorStats.compute(statuses)
            )
        }
    }
}