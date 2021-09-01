package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLTypeIndicatorViewProjectReportIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Getting report for a view`() {
        // Creating a view with 1 category
        val category = category()
        val types = (1..3).map { no ->
            category.booleanType(id = "${category.id}-$no")
        }
        val view = indicatorView(categories = listOf(category))
        // Projects with indicator values for this view's category
        val projects = (1..3).map { project() }
        projects.forEach { project ->
            types.forEachIndexed { index, type ->
                project.indicator(type, index % 2 == 0) // true, false, true ==> 100, 0, 100 ==> 66 avg
            }
        }
        // Projects without indicator values for this view's category
        val unsetProjects = (1..2).map { project() }
        // Getting the view report
        run(
            """
            {
                indicatorViewList {
                    views(id: "${view.id}") {
                        reports {
                            project {
                                name
                            }
                            viewStats {
                                category {
                                    name
                                }
                                stats {
                                    min
                                    avg
                                    max
                                    avgRating
                                }
                            }
                        }
                    }
                }
            }
        """
        ).let { data ->
            val reports = data.path("indicatorViewList").path("views").first().path("reports")

            /**
             * Reports indexed by project
             */

            val projectReports = reports.associate { report ->
                report.path("project").path("name").asText() to report.path("viewStats")
            }

            // Projects with values
            projects.forEach { project ->
                val projectStats = projectReports[project.name]
                assertNotNull(projectStats) { stats ->
                    assertEquals(1, stats.size())
                    val categoryStats = stats.first()
                    assertEquals(category.name, categoryStats.path("category").path("name").asText())
                    assertEquals(0, categoryStats.path("stats").path("min").asInt())
                    assertEquals(66, categoryStats.path("stats").path("avg").asInt())
                    assertEquals(100, categoryStats.path("stats").path("max").asInt())
                    assertEquals("C", categoryStats.path("stats").path("avgRating").asText())
                }
            }
            // Projects without value
            unsetProjects.forEach { project ->
                val projectStats = projectReports[project.name]
                assertNotNull(projectStats) { stats ->
                    assertEquals(1, stats.size())
                    val categoryStats = stats.first()
                    assertEquals(category.name, categoryStats.path("category").path("name").asText())
                    assertJsonNull(categoryStats.path("stats").path("min"))
                    assertJsonNull(categoryStats.path("stats").path("avg"))
                    assertJsonNull(categoryStats.path("stats").path("max"))
                    assertJsonNull(categoryStats.path("stats").path("avgRating"))
                }
            }
        }
    }

}