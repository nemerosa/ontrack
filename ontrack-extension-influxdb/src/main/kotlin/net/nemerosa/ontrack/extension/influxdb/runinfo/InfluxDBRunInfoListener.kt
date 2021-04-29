package net.nemerosa.ontrack.extension.influxdb.runinfo

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.influxdb.InfluxDBConnection
import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoListener
import net.nemerosa.ontrack.model.structure.RunnableEntity
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit

class InfluxDBRunInfoListener(
        private val influxDBConnection: InfluxDBConnection
) : RunInfoListener {

    override fun onRunInfoCreated(runnableEntity: RunnableEntity, runInfo: RunInfo) {
        val runTime = runInfo.runTime
        if (runTime != null) {
            influxDBConnection.safe {
                write(
                    Point.measurement("ontrack_value_run_info_${runnableEntity.runnableEntityType.name.toLowerCase()}_time_seconds")
                        .tag(runnableEntity.runMetricTags)
                        .addField("value", runTime)
                        .addField("name", runnableEntity.runMetricName)
                        .time(Time.toEpochMillis(runnableEntity.runTime), TimeUnit.MILLISECONDS)
                        .build()
                )
                flush()
            }
        }
    }

}