package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build

interface ChangeLogTemplatingService {

    fun render(
        fromBuild: Build,
        toBuild: Build,
        configMap: Map<String, String>,
        renderer: EventRenderer,
    ): String

}