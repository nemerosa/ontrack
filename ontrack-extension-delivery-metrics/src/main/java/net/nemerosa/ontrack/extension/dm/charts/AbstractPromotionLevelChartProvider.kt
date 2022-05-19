package net.nemerosa.ontrack.extension.dm.charts

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.Chart
import net.nemerosa.ontrack.extension.chart.ChartProvider
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService

abstract class AbstractPromotionLevelChartProvider<C : Chart>(
    protected val structureService: StructureService,
) : ChartProvider<PromotionLevelChartParameters, C> {

    override fun parseParameters(data: JsonNode): PromotionLevelChartParameters = data.parse()

    override fun getChart(options: GetChartOptions, parameters: PromotionLevelChartParameters): C {
        val pl = structureService.getPromotionLevel(ID.of(parameters.id))
        // Gets the promotion runs in this period
        val runs: List<PromotionRun> =
            structureService.getPromotionRunsForPromotionLevel(pl.id).filter { run ->
                run.signature.time in options.actualInterval
            }
        // Gets the chart data
        return getChart(runs, options)
    }

    abstract fun getChart(runs: List<PromotionRun>, options: GetChartOptions): C
}