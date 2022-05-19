package net.nemerosa.ontrack.extension.chart.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.Chart
import net.nemerosa.ontrack.extension.chart.ChartProvider
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun

abstract class AbstractValidationStampChartProvider<C: Chart>(
    protected val structureService: StructureService,
) : ChartProvider<ValidationStampChartParameters,C> {

    override fun parseParameters(data: JsonNode): ValidationStampChartParameters = data.parse()

    override fun getChart(options: GetChartOptions, parameters: ValidationStampChartParameters): C {
        val vs = structureService.getValidationStamp(ID.of(parameters.id))
        // Gets the validation runs in this period
        val runs: List<ValidationRun> =
            structureService.getValidationRunsForValidationStampBetweenDates(
                vs.id,
                options.actualInterval.start,
                options.actualInterval.end
            )
        // Gets the chart data
        return getChart(runs, options)
    }

    abstract fun getChart(runs: List<ValidationRun>, options: GetChartOptions): C
}