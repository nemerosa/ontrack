package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningDispatcher
import net.nemerosa.ontrack.extension.av.settings.AutoVersioningSettings
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTracking
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.springframework.stereotype.Component

/**
 * Entry point of the whole "auto versioning on promotion".
 */
@Component
class AutoVersioningPromotionListener(
    private val cachedSettingsService: CachedSettingsService,
    private val autoVersioningEventService: AutoVersioningPromotionListenerService,
    private val autoVersioningDispatcher: AutoVersioningDispatcher,
    private val autoVersioningTrackingService: AutoVersioningTrackingService,
) : EventListener {

    override fun onEvent(event: Event) {
        if (event.eventType == EventFactory.NEW_PROMOTION_RUN) {
            val settings = cachedSettingsService.getCachedSettings(AutoVersioningSettings::class.java)
            if (settings.enabled) {
                // Gets information about the event
                val run: PromotionRun = event.getEntity(ProjectEntityType.PROMOTION_RUN)
                // Initiating a trail
                val tracking = autoVersioningTrackingService.start(run)
                // Gets the list of configured branches
                val configuredBranches = autoVersioningEventService.getConfiguredBranches(run, tracking)
                // Dispatching
                autoVersioningDispatcher.dispatch(configuredBranches, tracking.trail)
            }
        }
    }

}