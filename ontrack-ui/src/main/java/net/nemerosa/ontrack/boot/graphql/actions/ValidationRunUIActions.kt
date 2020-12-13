package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.graphql.schema.actions.UIAction
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component

@Component
class ValidationRunUIActions(
    uriBuilder: URIBuilder
): SimpleUIActionsProvider<ValidationRun>(ValidationRun::class, uriBuilder) {
    override val actions: List<UIAction<ValidationRun>> = listOf(
        
    )
}