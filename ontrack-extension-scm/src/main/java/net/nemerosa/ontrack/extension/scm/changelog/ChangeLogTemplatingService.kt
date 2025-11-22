package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build

interface ChangeLogTemplatingService {

    @Deprecated("Use the typed method, without the config map.")
    fun render(
        fromBuild: Build,
        toBuild: Build,
        configMap: Map<String, String>,
        renderer: EventRenderer,
    ): String

    /**
     * Renders a changelog between two builds, given a number of options.
     *
     * @param fromBuild From which build do we want to see the changelog?
     * @param toBuild To which build do we want to see the changelog?
     * @param config Configuration for the rendering
     * @param renderer Renderer for the events
     * @return Rendered template
     */
    fun render(
        fromBuild: Build,
        toBuild: Build,
        config: ChangeLogTemplatingServiceConfig,
        renderer: EventRenderer,
    ): String

}