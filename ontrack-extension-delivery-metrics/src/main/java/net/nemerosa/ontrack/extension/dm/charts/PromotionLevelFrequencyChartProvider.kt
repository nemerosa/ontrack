package net.nemerosa.ontrack.extension.dm.charts

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.chart.support.CountChart
import net.nemerosa.ontrack.extension.chart.support.CountChartItemData
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class PromotionLevelFrequencyChartProvider(
    structureService: StructureService,
) : AbstractPromotionLevelChartProvider(
    structureService
) {

    override val name: String = "promotion-level-frequency"

    override fun getChart(runs: List<PromotionRun>, options: GetChartOptions): JsonNode =
        CountChart.compute(
            items = runs.map { run ->
                CountChartItemData(
                    timestamp = run.signature.time,
                )
            },
            interval = options.actualInterval,
            period = options.period,
        ).asJson()

}