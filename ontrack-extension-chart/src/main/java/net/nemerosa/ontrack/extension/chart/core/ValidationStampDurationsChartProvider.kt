package net.nemerosa.ontrack.extension.chart.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

@Component
class ValidationStampDurationsChartProvider(
    structureService: StructureService,
) : AbstractValidationStampChartProvider(
    structureService,
) {

    override val name: String = "validation-stamp-durations"

    override fun getChart(runs: List<ValidationRun>, options: GetChartOptions): JsonNode {
        TODO("Not yet implemented")
    }
}