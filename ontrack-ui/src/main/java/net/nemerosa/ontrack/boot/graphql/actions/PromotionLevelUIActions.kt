package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.PromotionLevelController
import net.nemerosa.ontrack.graphql.schema.actions.UIAction
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class PromotionLevelUIActions(
        uriBuilder: URIBuilder
) : SimpleUIActionsProvider<PromotionLevel>(PromotionLevel::class, uriBuilder) {
    override val actions: List<UIAction<PromotionLevel>> = listOf(
            downloadUpload(
                    "image",
                    "Promotion level image",
                    download = { on(PromotionLevelController::class.java).getPromotionLevelImage_(null, it.id) },
                    upload = { on(PromotionLevelController::class.java).setPromotionLevelImage(it.id, null) }
            )
    )
}
