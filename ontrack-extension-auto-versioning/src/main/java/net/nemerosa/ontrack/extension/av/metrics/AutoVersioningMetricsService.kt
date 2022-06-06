package net.nemerosa.ontrack.extension.av.metrics

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningProcessingOutcome

interface AutoVersioningMetricsService {

    fun onQueuing(order: AutoVersioningOrder, routingKey: String)

    fun onReceiving(order: AutoVersioningOrder, queue: String?)

    fun onProcessingCompleted(order: AutoVersioningOrder, outcome: AutoVersioningProcessingOutcome)

    fun onProcessingUncaughtError()

}