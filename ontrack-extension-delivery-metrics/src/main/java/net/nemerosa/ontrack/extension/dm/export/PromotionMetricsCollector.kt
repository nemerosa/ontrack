package net.nemerosa.ontrack.extension.dm.export

interface PromotionMetricsCollector {

    fun createWorker(): PromotionMetricsWorker

}