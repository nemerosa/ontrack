package net.nemerosa.ontrack.extension.dm.charts

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.chart.ChartService
import net.nemerosa.ontrack.extension.chart.GetChartInput
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

internal class PromotionLevelChartsIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var chartService: ChartService

    @Test
    fun `Promotion level frequency`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    val now = Time.now()
                    val ref = now.minusDays(28)
                    val promotions = setOf(
                        0L, 1, 2, 3, 5, // 1st week
                        8, 13, // 2nd week
                        21, // 3rd week
                    )
                    (0L..28).forEach { day ->
                        val time = ref.plusDays(day)
                        val build = build(name = day.toString()) {
                            updateBuildSignature(time = time)
                        }
                        if (day in promotions) {
                            build.promote(
                                pl, signature = Signature.Companion.of(
                                    time,
                                    "test"
                                )
                            )
                        }
                    }

                    val data = chartService.getChart(
                        GetChartInput(
                            name = "promotion-level-frequency",
                            options = GetChartOptions(
                                ref = now,
                                interval = "4w",
                                period = "1w"
                            ),
                            parameters = mapOf("id" to pl.id()).asJson()
                        )
                    )

                    assertEquals(
                        mapOf(
                            "dates" to (-4L..-1).map { n ->
                                now.plusWeeks(n).format(DateTimeFormatter.ISO_DATE)
                            },
                            "data" to listOf(
                                4.0,
                                2.0,
                                1.0,
                                0.0
                            )
                        ).asJson(),
                        data
                    )
                }
            }
        }
    }

}