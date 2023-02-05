package net.nemerosa.ontrack.git.model.plot

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GPlotTest {

    @Test
    fun `GPlot to JSON`() {
        val gplot = GPlot()
        gplot.add(
            GLine(
                GColor(0),
                GPoint(0, 10),
                GPoint(0, 20),
                3
            )
        )
        gplot.add(
            GOval(
                GColor(2),
                GPoint(0, 20),
                GDim(3, 3),
            )
        )
        gplot.add(
            GLine(
                GColor(1),
                GPoint(0, 20),
                GPoint(0, 30),
                3
            )
        )
        assertEquals(
            mapOf(
                "items" to listOf(
                    mapOf(
                        "color" to mapOf(
                            "index" to 0
                        ),
                        "a" to mapOf(
                            "x" to 0,
                            "y" to 10,
                            "maxX" to 0,
                            "maxY" to 10,
                            "type" to "point"
                        ),
                        "b" to mapOf(
                            "x" to 0,
                            "y" to 20,
                            "maxX" to 0,
                            "maxY" to 20,
                            "type" to "point"
                        ),
                        "width" to 3,
                        "maxX" to 0,
                        "maxY" to 20,
                        "type" to "line"
                    ),
                    mapOf(
                        "color" to mapOf(
                            "index" to 2
                        ),
                        "c" to mapOf(
                            "x" to 0,
                            "y" to 20,
                            "maxX" to 0,
                            "maxY" to 20,
                            "type" to "point"
                        ),
                        "d" to mapOf(
                            "w" to 3,
                            "h" to 3
                        ),
                        "maxX" to 3,
                        "maxY" to 23,
                        "type" to "oval"
                    ),
                    mapOf(
                        "color" to mapOf(
                            "index" to 1
                        ),
                        "a" to mapOf(
                            "x" to 0,
                            "y" to 20,
                            "maxX" to 0,
                            "maxY" to 20,
                            "type" to "point"
                        ),
                        "b" to mapOf(
                            "x" to 0,
                            "y" to 30,
                            "maxX" to 0,
                            "maxY" to 30,
                            "type" to "point"
                        ),
                        "width" to 3,
                        "maxX" to 0,
                        "maxY" to 30,
                        "type" to "line"
                    )
                ),
                "width" to 3,
                "height" to 30,
            ).asJson(),
            gplot.asJson()
        )
    }

}