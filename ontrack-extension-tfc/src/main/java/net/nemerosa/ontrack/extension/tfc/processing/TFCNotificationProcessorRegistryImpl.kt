package net.nemerosa.ontrack.extension.tfc.processing

import org.springframework.stereotype.Component

@Component
class TFCNotificationProcessorRegistryImpl(
    processors: List<TFCNotificationProcessor<*>>,
) : TFCNotificationProcessorRegistry {

    private val index = processors.associateBy { it.trigger }

    @Suppress("UNCHECKED_CAST")
    override fun <P> findProcessorByTrigger(trigger: String): TFCNotificationProcessor<P>? =
        index[trigger] as? TFCNotificationProcessor<P>?

}