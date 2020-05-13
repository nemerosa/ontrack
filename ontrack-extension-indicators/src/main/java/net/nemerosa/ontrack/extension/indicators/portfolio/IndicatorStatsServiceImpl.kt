package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import org.springframework.stereotype.Service

@Service
class IndicatorStatsServiceImpl(
        private val indicatorPortfolioService: IndicatorPortfolioService,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val indicatorTypeService: IndicatorTypeService,
        private val indicatorService: IndicatorService
) : IndicatorStatsService {

    override fun getGlobalStats(portfolio: IndicatorPortfolio): List<IndicatorCategoryStats> {
        // Gets the global categories
        val categories = indicatorPortfolioService.getPortfolioOfPortfolios().categories.mapNotNull {
            indicatorCategoryService.findCategoryById(it)
        }
        // Gets the projects for this portfolio
        val projects = indicatorPortfolioService.getPortfolioProjects(portfolio)
        // For each category
        return categories.map { category ->
            // Gets all types for this category
            val types = indicatorTypeService.findByCategory(category)
            // Gets all the indicators for all projects and types
            val compliances = types.flatMap { type ->
                projects.map { project ->
                    indicatorService.getProjectIndicator(project, type).compliance
                }
            }
            // Computation
            IndicatorCategoryStats(
                    category = category,
                    stats = IndicatorStats.compute(compliances)
            )
        }
    }
}