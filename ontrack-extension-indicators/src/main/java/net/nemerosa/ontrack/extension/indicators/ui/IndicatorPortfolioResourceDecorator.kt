package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.extension.indicators.portfolio.PortfolioUpdateForm
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class IndicatorPortfolioResourceDecorator : AbstractLinkResourceDecorator<IndicatorPortfolio>(IndicatorPortfolio::class.java) {
    override fun getLinkDefinitions(): Iterable<LinkDefinition<IndicatorPortfolio>> = listOf(

            Link.UPDATE linkTo { p: IndicatorPortfolio ->
                on(IndicatorPortfolioController::class.java).updatePortfolio(p.id, PortfolioUpdateForm())
            } linkIfGlobal IndicatorPortfolioManagement::class

    )
}