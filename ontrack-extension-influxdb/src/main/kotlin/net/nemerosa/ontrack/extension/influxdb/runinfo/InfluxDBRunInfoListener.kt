package net.nemerosa.ontrack.extension.influxdb.runinfo

import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoListener
import net.nemerosa.ontrack.model.structure.RunnableEntity
import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@ConditionalOnBean(InfluxDB::class)
class InfluxDBRunInfoListener(
        private val influxDB: InfluxDB
) : RunInfoListener {

    override fun onRunInfoCreated(runnableEntity: RunnableEntity, runInfo: RunInfo) {
        val runTime = runInfo.runTime
        if (runTime != null) {
            influxDB.write(
                    Point.measurement("ontrack_run_info_${runnableEntity.runnableEntityType.name.toLowerCase()}_time_seconds")
                            .tag(runnableEntity.runMetricTags)
                            .addField("value", runTime)
                            .addField("name", runnableEntity.runMetricName)
                            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                            .build()
            )
            influxDB.flush()
        }
    }

}