package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer

interface SemanticChangelogRenderingService {

    fun render(
        changelog: SCMChangeLog,
        config: SemanticChangeLogConfig,
        suffix: String?,
        renderer: EventRenderer
    ): String

}