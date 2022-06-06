package net.nemerosa.ontrack.extension.av.metrics

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

interface AutoVersioningMetricsService {

    fun onQueuing(order: AutoVersioningOrder, routingKey: String)

    fun onReceiving(order: AutoVersioningOrder, queue: String?)

}