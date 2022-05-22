package net.nemerosa.ontrack.extension.dm.charts

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.chart.ChartProvider
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.chart.support.CountChart
import net.nemerosa.ontrack.extension.chart.support.CountChartItemData
import net.nemerosa.ontrack.extension.dm.data.EndToEndPromotionFilter
import net.nemerosa.ontrack.extension.dm.data.EndToEndPromotionsHelper
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class PromotionLevelFrequencyChartProvider(
    private val endToEndPromotionsHelper: EndToEndPromotionsHelper,
) : ChartProvider<PromotionLevelChartParameters, CountChart> {

    override val name: String = "promotion-level-frequency"

    override fun parseParameters(data: JsonNode): PromotionLevelChartParameters = data.parse()

    override fun getChart(options: GetChartOptions, parameters: PromotionLevelChartParameters): CountChart {
        val filter = EndToEndPromotionFilter(
            maxDepth = 1,
            promotionId = parameters.id,
            afterTime = options.actualInterval.start,
            beforeTime = options.actualInterval.end,
        )
        val items = mutableListOf<CountChartItemData>()
        endToEndPromotionsHelper.forEachEndToEndPromotionRecord(filter) { record ->
            val promotionCreation = record.ref.promotionCreation
            if (promotionCreation != null) {
                items += CountChartItemData(
                    timestamp = promotionCreation
                )
            }
        }
        return CountChart.compute(
            items,
            interval = options.actualInterval,
            period = options.period,
        )
    }

}