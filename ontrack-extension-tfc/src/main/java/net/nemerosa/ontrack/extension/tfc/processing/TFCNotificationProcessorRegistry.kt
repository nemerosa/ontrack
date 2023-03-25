package net.nemerosa.ontrack.extension.tfc.processing

interface TFCNotificationProcessorRegistry {

    fun <P> findProcessorByTrigger(trigger: String): TFCNotificationProcessor<P>?

}