package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningDispatcher
import net.nemerosa.ontrack.extension.av.settings.AutoVersioningSettings
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.settings.CachedSettingsService
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
) : EventListener {

    override fun onEvent(event: Event) {
        if (event.eventType == EventFactory.NEW_PROMOTION_RUN) {
            val settings = cachedSettingsService.getCachedSettings(AutoVersioningSettings::class.java)
            if (settings.enabled) {
                // Gets information about the event
                val run: PromotionRun = event.getEntity(ProjectEntityType.PROMOTION_RUN)
                // Gets the list of configured branches
                val configuredBranches = autoVersioningEventService.getConfiguredBranches(run)
                // Dispatching
                if (configuredBranches != null) {
                    autoVersioningDispatcher.dispatch(configuredBranches)
                }
            }
        }
    }

}