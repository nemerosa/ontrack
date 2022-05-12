package net.nemerosa.ontrack.extension.chart.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.chart.support.*
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

@Component
class ValidationStampStabilityChartProvider(
    structureService: StructureService,
) : AbstractValidationStampChartProvider(
    structureService,
) {

    override val name: String = "validation-stamp-stability"

    override fun getChart(runs: List<ValidationRun>, options: GetChartOptions): JsonNode =
        PercentageChart.compute(
            items = runs.map { run ->
                PercentageChartItemData(
                    timestamp = run.lastStatus.signature.time,
                    value = ChartUtils.percentageFromBoolean(run.lastStatus.isPassed)
                )
            },
            interval = options.actualInterval,
            period = options.period,
        ).asJson()
}