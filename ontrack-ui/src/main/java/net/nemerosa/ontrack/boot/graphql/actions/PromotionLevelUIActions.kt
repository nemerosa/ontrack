package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.PromotionLevelController
import net.nemerosa.ontrack.graphql.schema.actions.UIAction
import net.nemerosa.ontrack.graphql.schema.actions.UIActionLink
import net.nemerosa.ontrack.graphql.schema.actions.UIActionLinks
import net.nemerosa.ontrack.graphql.schema.actions.UIActionsProvider
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import kotlin.reflect.KClass

@Component
class PromotionLevelUIActions(
        uriBuilder: URIBuilder
) : UIActionsProvider<PromotionLevel> {
    override val targetType: KClass<PromotionLevel> = PromotionLevel::class
    override val actions: List<UIAction<PromotionLevel>> = listOf(
            UIAction(
                    "image",
                    "Promotion level image",
                    listOf(
                            UIActionLink(
                                    UIActionLinks.DOWNLOAD,
                                    "Download image",
                                    HttpMethod.GET
                            ) { uriBuilder.build(on(PromotionLevelController::class.java).getPromotionLevelImage_(null, it.id)) },
                            UIActionLink(
                                    UIActionLinks.UPDATE,
                                    "Upload image",
                                    HttpMethod.POST
                            ) { uriBuilder.build(on(PromotionLevelController::class.java).setPromotionLevelImage(it.id, null)) }
                    ),
                    null
            )
    )
}