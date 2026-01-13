package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig

interface PromotionChangeLogTemplatingService {

    fun render(
        toBuild: Build,
        promotion: String,
        config: TemplatingSourceConfig,
        renderer: EventRenderer,
    ): String

}