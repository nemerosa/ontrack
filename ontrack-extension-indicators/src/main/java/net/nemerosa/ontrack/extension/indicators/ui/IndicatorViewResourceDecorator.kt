package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.linkIfGlobal
import net.nemerosa.ontrack.ui.resource.linkTo
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class IndicatorViewResourceDecorator : AbstractLinkResourceDecorator<IndicatorView>(IndicatorView::class.java) {
    override fun getLinkDefinitions() = listOf(

        Link.UPDATE linkTo { v: IndicatorView ->
            on(IndicatorViewController::class.java)
                .update(v.id, IndicatorViewController.IndicatorViewForm("", emptyList()))
        } linkIfGlobal IndicatorViewManagement::class,

        Link.DELETE linkTo { v: IndicatorView ->
            on(IndicatorViewController::class.java)
                .delete(v.id)
        } linkIfGlobal IndicatorViewManagement::class

    )
}