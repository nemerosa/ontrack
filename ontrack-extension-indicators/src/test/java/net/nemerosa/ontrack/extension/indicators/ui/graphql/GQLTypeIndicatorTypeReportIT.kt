package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.test.assertJsonNotNull
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GQLTypeIndicatorTypeReportIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Getting indicator type values for all projects`() {
        val type = category().integerType()
        val projects = (1..3).map { project() }
        projects.forEach { project ->
            project.indicator(type, project.id())
        }
        val unsetProjects = (1..2).map { project() }

        run(
            """
            {
                indicatorTypes {
                    types(id: "${type.id}") {
                        indicators {
                            project {
                                name
                            }
                            value
                        }
                    }
                }
            }
        """
        ).let { data ->
            val indicators = data.path("indicatorTypes").path("types").first().path("indicators")
            val projectIndicators =
                indicators.associateBy { projectIndicator -> projectIndicator.path("project").path("name").asText() }

            // Projects with values
            projects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertNotNull(projectIndicator) {
                    val value = projectIndicator?.path("value")?.path("value")
                    assertJsonNotNull(value) {
                        assertTrue(this.isInt)
                        assertEquals(project.id(), this.asInt())
                    }
                }
            }
            // Projects without value
            unsetProjects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertNotNull(projectIndicator) {
                    val value = projectIndicator?.path("value")?.path("value")
                    assertJsonNull(value)
                }
            }
        }
    }

    @Test
    fun `Getting indicator type values only for projects having an indicator`() {
        val type = category().integerType()
        val projects = (1..3).map { project() }
        projects.forEach { project ->
            project.indicator(type, project.id())
        }
        val unsetProjects = (1..2).map { project() }

        run(
            """
            {
                indicatorTypes {
                    types(id: "${type.id}") {
                        indicators(filledOnly: true) {
                            project {
                                name
                            }
                            value
                        }
                    }
                }
            }
        """
        ).let { data ->
            val indicators = data.path("indicatorTypes").path("types").first().path("indicators")
            val projectIndicators =
                indicators.associateBy { projectIndicator -> projectIndicator.path("project").path("name").asText() }

            // Projects with values
            projects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertNotNull(projectIndicator) {
                    val value = projectIndicator?.path("value")?.path("value")
                    assertJsonNotNull(value) {
                        assertTrue(this.isInt)
                        assertEquals(project.id(), this.asInt())
                    }
                }
            }
            // Projects without value
            unsetProjects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertJsonNull(projectIndicator)
            }
        }
    }

    @Test
    fun `Getting indicator type values for a project identified by name`() {
        val type = category().integerType()
        val projects = (1..3).map { project() }
        projects.forEach { project ->
            project.indicator(type, project.id())
        }
        val unsetProjects = (1..2).map { project() }

        run(
            """
            {
                indicatorTypes {
                    types(id: "${type.id}") {
                        indicators(projectName: "${projects[0].name}") {
                            project {
                                name
                            }
                            value
                        }
                    }
                }
            }
        """
        ).let { data ->
            val indicators = data.path("indicatorTypes").path("types").first().path("indicators")
            val projectIndicators =
                indicators.associateBy { projectIndicator -> projectIndicator.path("project").path("name").asText() }

            // Projects with values
            projects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                if (project.name == projects[0].name) {
                    assertNotNull(projectIndicator) {
                        val value = projectIndicator?.path("value")?.path("value")
                        assertJsonNotNull(value) {
                            assertTrue(this.isInt)
                            assertEquals(project.id(), this.asInt())
                        }
                    }
                } else {
                    assertJsonNull(projectIndicator)
                }
            }
            // Projects without value
            unsetProjects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertJsonNull(projectIndicator)
            }
        }
    }

    @Test
    fun `Getting indicator type values for a project identified by ID`() {
        val type = category().integerType()
        val projects = (1..3).map { project() }
        projects.forEach { project ->
            project.indicator(type, project.id())
        }
        val unsetProjects = (1..2).map { project() }

        run(
            """
            {
                indicatorTypes {
                    types(id: "${type.id}") {
                        indicators(projectId: ${projects[0].id}) {
                            project {
                                name
                            }
                            value
                        }
                    }
                }
            }
        """
        ).let { data ->
            val indicators = data.path("indicatorTypes").path("types").first().path("indicators")
            val projectIndicators =
                indicators.associateBy { projectIndicator -> projectIndicator.path("project").path("name").asText() }

            // Projects with values
            projects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                if (project.name == projects[0].name) {
                    assertNotNull(projectIndicator) {
                        val value = projectIndicator?.path("value")?.path("value")
                        assertJsonNotNull(value) {
                            assertTrue(this.isInt)
                            assertEquals(project.id(), this.asInt())
                        }
                    }
                } else {
                    assertJsonNull(projectIndicator)
                }
            }
            // Projects without value
            unsetProjects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertJsonNull(projectIndicator)
            }
        }
    }

    @Test
    fun `Getting indicator type values for projects identified by a portfolio`() {
        val type = category().integerType()
        val projects = (1..3).map { project() }
        projects.forEach { project ->
            project.indicator(type, project.id())
        }
        val unsetProjects = (1..2).map { project() }

        val label = label()
        val portfolio = portfolio(categories = listOf(type.category), label = label)
        projects[0].labels = listOf(label)

        run(
            """
            {
                indicatorTypes {
                    types(id: "${type.id}") {
                        indicators(portfolio: "${portfolio.id}") {
                            project {
                                name
                            }
                            value
                        }
                    }
                }
            }
        """
        ).let { data ->
            val indicators = data.path("indicatorTypes").path("types").first().path("indicators")
            val projectIndicators =
                indicators.associateBy { projectIndicator -> projectIndicator.path("project").path("name").asText() }

            // Projects with values
            projects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                if (project.name == projects[0].name) {
                    assertNotNull(projectIndicator) {
                        val value = projectIndicator?.path("value")?.path("value")
                        assertJsonNotNull(value) {
                            assertTrue(this.isInt)
                            assertEquals(project.id(), this.asInt())
                        }
                    }
                } else {
                    assertJsonNull(projectIndicator)
                }
            }
            // Projects without value
            unsetProjects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertJsonNull(projectIndicator)
            }
        }
    }

    @Test
    fun `Getting indicator type values for projects identified by a label`() {
        val type = category().integerType()
        val projects = (1..3).map { project() }
        projects.forEach { project ->
            project.indicator(type, project.id())
        }
        val unsetProjects = (1..2).map { project() }

        val label = label()
        projects[0].labels = listOf(label)

        run(
            """
            {
                indicatorTypes {
                    types(id: "${type.id}") {
                        indicators(label: "${label.getDisplay()}") {
                            project {
                                name
                            }
                            value
                        }
                    }
                }
            }
        """
        ).let { data ->
            val indicators = data.path("indicatorTypes").path("types").first().path("indicators")
            val projectIndicators =
                indicators.associateBy { projectIndicator -> projectIndicator.path("project").path("name").asText() }

            // Projects with values
            projects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                if (project.name == projects[0].name) {
                    assertNotNull(projectIndicator) {
                        val value = projectIndicator?.path("value")?.path("value")
                        assertJsonNotNull(value) {
                            assertTrue(this.isInt)
                            assertEquals(project.id(), this.asInt())
                        }
                    }
                } else {
                    assertJsonNull(projectIndicator)
                }
            }
            // Projects without value
            unsetProjects.forEach { project ->
                val projectIndicator = projectIndicators[project.name]
                assertJsonNull(projectIndicator)
            }
        }
    }

}