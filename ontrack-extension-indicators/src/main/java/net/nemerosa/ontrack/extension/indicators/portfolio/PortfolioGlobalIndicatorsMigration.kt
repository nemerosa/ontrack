package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Migration of the global indicators into a 'Migrated' indicator view.
 */
@Deprecated("Used for the transition period in V4. This class will be removed in V4.")
@Component
class PortfolioGlobalIndicatorsMigration(
    private val indicatorPortfolioService: IndicatorPortfolioService,
    private val indicatorViewService: IndicatorViewService,
    private val securityService: SecurityService
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(PortfolioGlobalIndicatorsMigration::class.java)

    override fun getName(): String = "Migration of global indicators to an indicator view"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        securityService.asAdmin {
            // Gets the migrated view
            val view = indicatorViewService.findIndicatorViewByName(DEFAULT_VIEW_NAME)
            if (view == null) {
                // Gets the global indicators
                val formerCategories = indicatorPortfolioService.getPortfolioOfPortfolios().categories
                if (formerCategories.isNotEmpty()) {
                    logger.info("""Migrating old global indicators to the "$DEFAULT_VIEW_NAME" indicator view.""")
                    indicatorViewService.saveIndicatorView(
                        IndicatorView(
                            id = "",
                            name = DEFAULT_VIEW_NAME,
                            categories = formerCategories
                        )
                    )
                    // Clears the global indicators
                    indicatorPortfolioService.savePortfolioOfPortfolios(
                        PortfolioGlobalIndicators(emptyList())
                    )
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_VIEW_NAME = "Migrated from global indicators"
    }
}