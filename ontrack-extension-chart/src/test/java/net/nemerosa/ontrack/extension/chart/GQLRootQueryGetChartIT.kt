package net.nemerosa.ontrack.extension.chart

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GQLRootQueryGetChartIT: AbstractQLKTITSupport() {

    @Test
    fun `Chart data for the validation stamp durations`() {
        val durations = listOf(1, 2, 3, 5, 8, 13, 21, 34)
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    // Creates several builds and validations with some date interval
                    val ref = Time.now().minusDays(25)
                    var index = 0
                    repeat(5) { buildNo ->
                        build(name = "build-$buildNo") {
                            repeat(5) { runNo ->
                                validate(vs, description = "run-$runNo", signature = Signature.of(
                                    ref.plusDays(index.toLong()),
                                    "test"
                                ), duration = durations[index % durations.size])
                                index++
                            }
                        }
                    }
                    // Gets the chart data
                    run("""{
                        getChart(input: {
                            name: "validation-stamp-durations"
                            options: {
                                interval: "1m",
                                period: "1w"
                            },
                            parameters: {
                                id: ${vs.id}
                            }
                        })
                    }""") { data ->
                        val chart = data.path("getChart")
                        // Checks the chart data
                        assertEquals(
                            mapOf(
                                "categories" to listOf("Mean", "90th percentile", "Maximum"),
                                "dates" to listOf("2022-04-12", "2022-04-19", "2022-04-26", "2022-05-03", "2022-05-10"),
                                "data" to mapOf(
                                    "mean" to listOf(2.0, 12.0, 12.142857142857142, 12.285714285714286, 1.0),
                                    "percentile90" to listOf(3.0, 34.0, 34.0, 34.0, 1.0),
                                    "maximum" to listOf(3.0, 34.0, 34.0, 34.0, 1.0)
                                )
                            ).asJson(),
                            chart
                        )
                    }
                }
            }
        }
    }

}