package net.nemerosa.ontrack.extension.chart.core

import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.chart.support.CountChart
import net.nemerosa.ontrack.extension.chart.support.CountChartItemData
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

@Component
class ValidationStampFrequencyChartProvider(
    structureService: StructureService,
) : AbstractValidationStampChartProvider<CountChart>(
    structureService,
) {

    override val name: String = "validation-stamp-frequency"

    override fun getChart(runs: List<ValidationRun>, options: GetChartOptions): CountChart =
        CountChart.compute(
            items = runs.map {
                CountChartItemData(
                    timestamp = it.lastStatus.signature.time
                )
            },
            interval = options.actualInterval,
            period = options.period,
        )
}