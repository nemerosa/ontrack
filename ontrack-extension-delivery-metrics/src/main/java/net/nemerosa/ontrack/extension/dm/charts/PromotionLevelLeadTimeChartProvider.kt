package net.nemerosa.ontrack.extension.dm.charts

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.ChartProvider
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.chart.support.DurationChart
import net.nemerosa.ontrack.extension.chart.support.DurationChartItemData
import net.nemerosa.ontrack.extension.dm.data.EndToEndPromotionFilter
import net.nemerosa.ontrack.extension.dm.data.EndToEndPromotionsHelper
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PromotionLevelLeadTimeChartProvider(
    private val endToEndPromotionsHelper: EndToEndPromotionsHelper,
) : ChartProvider<PromotionLevelChartParameters, DurationChart> {

    override val name: String = "promotion-level-lead-time"

    override fun parseParameters(data: JsonNode): PromotionLevelChartParameters = data.parse()

    override fun getChart(options: GetChartOptions, parameters: PromotionLevelChartParameters): DurationChart {
        val filter = EndToEndPromotionFilter(
            maxDepth = 1,
            promotionId = parameters.id,
            afterTime = options.actualInterval.start,
            beforeTime = options.actualInterval.end,
        )
        val items = mutableListOf<DurationChartItemData>()
        endToEndPromotionsHelper.forEachEndToEndPromotionRecord(filter) { record ->
            val buildCreation = record.ref.buildCreation
            val promotionCreation = record.ref.promotionCreation
            if (promotionCreation != null) {
                val duration = Duration.between(buildCreation, promotionCreation).toSeconds()
                items += DurationChartItemData(
                    timestamp = buildCreation,
                    value = duration.toDouble(),
                )
            }
        }
        return DurationChart.compute(
            items,
            interval = options.actualInterval,
            period = options.period,
        )
    }

}