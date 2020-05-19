package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioIndicatorManagement
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioOfPortfolios
import net.nemerosa.ontrack.extension.indicators.portfolio.PortfolioGlobalIndicators
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class IndicatorPortfolioOfPortfoliosResourceDecorator : AbstractLinkResourceDecorator<IndicatorPortfolioOfPortfolios>(IndicatorPortfolioOfPortfolios::class.java) {

    @Suppress("RedundantLambdaArrow")
    override fun getLinkDefinitions(): Iterable<LinkDefinition<IndicatorPortfolioOfPortfolios>> = listOf(

            Link.CREATE linkTo { _: IndicatorPortfolioOfPortfolios ->
                on(IndicatorPortfolioController::class.java).getPortfolioCreationForm()
            } linkIfGlobal IndicatorPortfolioManagement::class,

            "_globalIndicators" linkTo { _: IndicatorPortfolioOfPortfolios ->
                on(IndicatorPortfolioController::class.java).updatePortfolioGlobalIndicators(PortfolioGlobalIndicators(emptyList()))
            } linkIfGlobal IndicatorPortfolioIndicatorManagement::class

    )
}