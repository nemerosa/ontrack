package net.nemerosa.ontrack.extension.influxdb.validation.data

import net.nemerosa.ontrack.extension.api.ValidationRunMetricsExtension
import net.nemerosa.ontrack.extension.influxdb.InfluxDBExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunData
import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit

class InfluxDBValidationRunMetricsExtension(
        influxDBExtensionFeature: InfluxDBExtensionFeature,
        private val validationDataTypeService: ValidationDataTypeService,
        private val influxDB: InfluxDB
) : AbstractExtension(influxDBExtensionFeature), ValidationRunMetricsExtension {

    override fun onValidationRun(validationRun: ValidationRun) {
        val validationRunData: ValidationRunData<*>? = validationRun.data
        if (validationRunData != null) {
            onValidationRunData(validationRun, validationRunData)
        }
    }

    private fun <T> onValidationRunData(
            validationRun: ValidationRun,
            validationRunData: ValidationRunData<T>
    ) {
        val dataType: ValidationDataType<Any, T>? = validationDataTypeService.getValidationDataType(validationRunData.descriptor.id)
        if (dataType != null) {
            val metrics: Map<String, *>? = dataType.getMetrics(validationRunData.data)
            if (metrics != null && metrics.isNotEmpty()) {
                influxDB.write(
                        Point.measurement("ontrack_value_validation_data")
                                // Tags
                                .tag("project", validationRun.project.name)
                                .tag("branch", validationRun.validationStamp.branch.name)
                                .tag("build", validationRun.build.name)
                                .tag("validation", validationRun.validationStamp.name)
                                .tag("status", validationRun.lastStatus.statusID.id)
                                // Type
                                .tag("type", validationRunData.descriptor.id)
                                // Fields
                                .fields(metrics)
                                // OK
                                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                                .build()
                )
                influxDB.flush()
            }
        }
    }
}