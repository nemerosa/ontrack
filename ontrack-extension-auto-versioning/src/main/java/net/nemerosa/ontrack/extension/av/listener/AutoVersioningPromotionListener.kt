package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningDispatcher
import net.nemerosa.ontrack.extension.av.settings.AutoVersioningSettings
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.springframework.stereotype.Component

/**
 * Entry point of the whole "auto versioning on promotion".
 */
@Component
class AutoVersioningPromotionListener(
    private val cachedSettingsService: CachedSettingsService,
    private val autoVersioningEventService: AutoVersioningEventService,
    private val autoVersioningDispatcher: AutoVersioningDispatcher,
) : EventListener {

    override fun onEvent(event: Event) {
        if (event.eventType == EventFactory.NEW_PROMOTION_RUN) {
            val settings = cachedSettingsService.getCachedSettings(AutoVersioningSettings::class.java)
            if (settings.enabled) {
                // Gets information about the event
                val build: Build = event.getEntity(ProjectEntityType.BUILD)
                val promotion: PromotionLevel = event.getEntity(ProjectEntityType.PROMOTION_LEVEL)
                // Gets the list of configured branches
                val configuredBranches = autoVersioningEventService.getConfiguredBranches(build, promotion)
                // Dispatching
                autoVersioningDispatcher.dispatch(configuredBranches)
            }
        }
    }

}