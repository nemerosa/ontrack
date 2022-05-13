package net.nemerosa.ontrack.extension.dm

import net.nemerosa.ontrack.extension.chart.ChartExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class DeliveryMetricsExtensionFeature(
    private val chartExtensionFeature: ChartExtensionFeature,
) : AbstractExtensionFeature(
    id = "delivery-metrics",
    name = "Delivery metrics",
    description = "Exposes delivery metrics and charts.",
    options = ExtensionFeatureOptions.DEFAULT
        .withDependency(chartExtensionFeature)
)