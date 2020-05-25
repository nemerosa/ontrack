package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class IndicatorCategoryResourceDecorator : AbstractLinkResourceDecorator<IndicatorCategory>(IndicatorCategory::class.java) {
    override fun getLinkDefinitions(): Iterable<LinkDefinition<IndicatorCategory>> = listOf(

            Link.UPDATE linkTo { t: IndicatorCategory ->
                on(IndicatorCategoryController::class.java).getUpdateForm(t.id)
            } linkIf { t: IndicatorCategory, rc: ResourceContext ->
                t.source == null && rc.isGlobalFunctionGranted(IndicatorTypeManagement::class.java)
            },

            Link.DELETE linkTo { t: IndicatorCategory ->
                on(IndicatorCategoryController::class.java).deleteCategory(t.id)
            } linkIf { t: IndicatorCategory, rc: ResourceContext ->
                t.source == null && rc.isGlobalFunctionGranted(IndicatorTypeManagement::class.java)
            }

    )
}