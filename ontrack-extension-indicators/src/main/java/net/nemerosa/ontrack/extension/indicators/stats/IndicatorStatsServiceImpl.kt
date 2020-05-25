package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorCategoryStats
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class IndicatorStatsServiceImpl(
        private val indicatorPortfolioService: IndicatorPortfolioService,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val indicatorTypeService: IndicatorTypeService,
        private val indicatorService: IndicatorService
) : IndicatorStatsService {

    override fun getStatsForCategoryAndProject(category: IndicatorCategory, project: Project, previous: Duration?): IndicatorCategoryStats {
        return indicatorCategoryStats(category, listOf(project), previous)
    }

    override fun getStatsPortfolio(portfolio: IndicatorPortfolio, previous: Duration?): List<IndicatorCategoryStats> {
        // Gets the categories for this portfolio
        val categories = portfolio.categories.mapNotNull {
            indicatorCategoryService.findCategoryById(it)
        }
        // Gets the projects for this portfolio
        val projects = indicatorPortfolioService.getPortfolioProjects(portfolio)
        // Getting the stats
        return categories.map { category ->
            indicatorCategoryStats(category, projects, previous)
        }
    }

    override fun getGlobalStats(portfolio: IndicatorPortfolio, previous: Duration?): List<IndicatorCategoryStats> {
        // Gets the global categories
        val categories = indicatorPortfolioService.getPortfolioOfPortfolios().categories.mapNotNull {
            indicatorCategoryService.findCategoryById(it)
        }
        // Gets the projects for this portfolio
        val projects = indicatorPortfolioService.getPortfolioProjects(portfolio)
        // For each category
        return categories.map { category ->
            indicatorCategoryStats(category, projects, previous)
        }
    }

    private fun indicatorCategoryStats(category: IndicatorCategory, projects: List<Project>, previous: Duration?): IndicatorCategoryStats {
        // Gets all types for this category
        val types = indicatorTypeService.findByCategory(category)
        // Gets all the indicators for all projects and types
        val currentStats = getStats(types, projects, null)
        // Past stats
        val previousStats: IndicatorPreviousStats? = previous?.let {
            getPreviousStats(types, projects, currentStats, it)
        }
        // Computation
        return IndicatorCategoryStats(
                category = category,
                stats = currentStats,
                previousStats = previousStats
        )
    }

    private fun getStats(types: List<IndicatorType<*, *>>, projects: List<Project>, previous: Duration?): IndicatorStats {
        val compliances = types.flatMap { type ->
            projects.map { project ->
                indicatorService.getProjectIndicator(project, type, previous).compliance
            }
        }
        return IndicatorStats.compute(compliances)
    }

    private fun getPreviousStats(types: List<IndicatorType<*, *>>, projects: List<Project>, currentStats: IndicatorStats, previous: Duration): IndicatorPreviousStats {
        // Previous stats
        val previousStats = getStats(types, projects, previous)
        // Trend computation
        val minTrend = trendBetween(previousStats.min, currentStats.min)
        val avgTrend = trendBetween(previousStats.avg, currentStats.avg)
        val maxTrend = trendBetween(previousStats.max, currentStats.max)
        // Finally...
        return IndicatorPreviousStats(
                previousStats,
                previous,
                minTrend,
                avgTrend,
                maxTrend
        )
    }
}