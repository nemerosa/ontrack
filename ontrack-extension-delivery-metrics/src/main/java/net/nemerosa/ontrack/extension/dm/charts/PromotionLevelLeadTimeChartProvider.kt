package net.nemerosa.ontrack.extension.dm.charts

import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.chart.support.DurationChart
import net.nemerosa.ontrack.extension.chart.support.DurationChartItemData
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PromotionLevelLeadTimeChartProvider(
    structureService: StructureService,
) : AbstractPromotionLevelChartProvider<DurationChart>(
    structureService
) {

    override val name: String = "promotion-level-lead-time"

    override fun getChart(runs: List<PromotionRun>, options: GetChartOptions): DurationChart =
        DurationChart.compute(
            items = runs.map { run ->
                // From the time of the build to the promotion creation
                val duration = Duration.between(run.build.signature.time, run.signature.time).toSeconds()
                DurationChartItemData(
                    // Time of the build
                    timestamp = run.build.signature.time,
                    value = duration.toDouble()
                )
            },
            interval = options.actualInterval,
            period = options.period,
        )

}