package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build

interface PromotionChangeLogTemplatingService {

    fun render(
        toBuild: Build,
        promotion: String,
        configMap: Map<String, String>,
        renderer: EventRenderer,
    ): String

}