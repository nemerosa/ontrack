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
    fun `Promotion level lead time`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    val now = Time.now()
                    val ref = now.minusDays(28)
                    val promotions: Map<Long, Long> = mapOf(
                        // 1st week
                        0L to 8,
                        1L to 7,
                        2L to 6,
                        3L to 5,
                        5L to 4,
                        // 2nd week
                        8L to 3,
                        13L to 2,
                        // 3rd week
                        21L to 1,
                    )
                    (0L..28).forEach { day ->
                        val time = ref.plusDays(day)
                        val build = build(name = day.toString()) {
                            updateBuildSignature(time = time)
                        }
                        val promotionTime = promotions[day]
                        if (promotionTime != null) {
                            build.promote(
                                pl, signature = Signature.of(
                                    time.plusHours(promotionTime),
                                    "test"
                                )
                            )
                        }
                    }

                    val data = chartService.getChart(
                        GetChartInput(
                            name = "promotion-level-lead-time",
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
                            "categories" to listOf(
                                "Mean",
                                "90th percentile",
                                "Maximum",
                            ),
                            "dates" to (-4L..-1).map { n ->
                                now.plusWeeks(n).format(DateTimeFormatter.ISO_DATE)
                            },
                            "data" to mapOf(
                                "mean" to listOf(
                                    19800.0,
                                    9000.0,
                                    3600.0,
                                    Double.NaN,
                                ),
                                "percentile90" to listOf(
                                    25200.0,
                                    10800.0,
                                    3600.0,
                                    Double.NaN,
                                ),
                                "maximum" to listOf(
                                    25200.0,
                                    10800.0,
                                    3600.0,
                                    Double.NaN,
                                ),
                            )
                        ).asJson(),
                        data
                    )
                }
            }
        }
    }

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
                                pl, signature = Signature.of(
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