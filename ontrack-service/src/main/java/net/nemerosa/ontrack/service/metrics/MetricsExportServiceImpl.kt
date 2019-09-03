package net.nemerosa.ontrack.service.metrics

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import org.springframework.stereotype.Service

@Service
class MetricsExportServiceImpl(
        private val extensionManager: ExtensionManager
) : MetricsExportService {

    override fun exportMetrics(metric: String, tags: Map<String, String>, fields: Map<String, Double>) {
        extensionManager.getExtensions(MetricsExportExtension::class.java).forEach {
            it.exportMetrics(metric, tags, fields)
        }
    }

}