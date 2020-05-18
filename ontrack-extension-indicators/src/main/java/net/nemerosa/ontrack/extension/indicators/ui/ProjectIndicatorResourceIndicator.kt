package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorEdit
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ProjectIndicatorResourceIndicator : AbstractLinkResourceDecorator<ProjectIndicator>(ProjectIndicator::class.java) {
    override fun getLinkDefinitions(): Iterable<LinkDefinition<ProjectIndicator>> = listOf(

            Link.UPDATE linkTo { i: ProjectIndicator ->
                on(IndicatorController::class.java).getUpdateFormForIndicator(
                        i.project.id,
                        i.type.id
                )
            } linkIf { i, rc ->
                i.type.source != null && rc.isProjectFunctionGranted(i.project, IndicatorEdit::class.java)
            },

            Link.DELETE linkTo { i: ProjectIndicator ->
                on(IndicatorController::class.java).deleteIndicator(
                        i.project.id,
                        i.type.id
                )
            } linkIf { i, rc ->
                i.type.source != null && rc.isProjectFunctionGranted(i.project, IndicatorEdit::class.java)
            }
    )
}