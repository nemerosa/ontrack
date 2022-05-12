package net.nemerosa.ontrack.extension.chart.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.chart.support.DurationChart
import net.nemerosa.ontrack.extension.chart.support.DurationChartItemData
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

@Component
class ValidationStampDurationsChartProvider(
    structureService: StructureService,
    private val runInfoService: RunInfoService,
) : AbstractValidationStampChartProvider(
    structureService,
) {

    override val name: String = "validation-stamp-durations"

    override fun getChart(runs: List<ValidationRun>, options: GetChartOptions): JsonNode =
        DurationChart.compute(
            items = runs.map { run ->
                val runInfo = runInfoService.getRunInfo(run)
                DurationChartItemData(
                    timestamp = run.lastStatus.signature.time,
                    value = runInfo?.runTime?.toDouble(),
                )
            },
            interval = options.actualInterval,
            period = options.period,
        ).asJson()
}