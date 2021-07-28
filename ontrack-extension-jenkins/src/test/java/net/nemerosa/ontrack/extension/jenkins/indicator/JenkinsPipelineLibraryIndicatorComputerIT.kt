package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.indicators.model.IndicatorComputedCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorComputedType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorComputedValue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JenkinsPipelineLibraryIndicatorComputerIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var computer: JenkinsPipelineLibraryIndicatorComputer

    @Autowired
    private lateinit var valueType: JenkinsPipelineLibraryIndicatorValueType

    @Test
    fun `Project not eligible if no Git configuration`() {
        project {
            assertFalse(computer.isProjectEligible(this), "Project not eligible if no Git configuration")
        }
    }

    @Test
    fun `Project not eligible if Git repository is not indexed`() {
        createRepo {
            file("Jenkinsfile", """
                @Library('pipeline-general@1.0.1') _
                
                pipeline()
            """.trimIndent())
            git("commit", "-m", "Commit of the Jenkinsfile")
        } and { repo, _ ->
            project {
                gitProject(repo, sync = false)
                assertFalse(computer.isProjectEligible(this), "Project not eligible if Git repository is not indexed")
            }
        }
    }

    @Test
    fun `Project eligible if Git repository is indexed`() {
        createRepo {
            file("Jenkinsfile", """
                @Library('pipeline-general@1.0.1') _
                
                pipeline()
            """.trimIndent())
            git("commit", "-m", "Commit of the Jenkinsfile")
        } and { repo, _ ->
            project {
                gitProject(repo, sync = true)
                assertTrue(computer.isProjectEligible(this), "Project eligible if Git repository is indexed")
            }
        }
    }

    @Test
    fun `No indicator value when no Git configuration`() {
        project {
            val indicators = computer.computeIndicators(project)
            assertTrue(indicators.isEmpty(), "No indicator")
        }
    }

    @Test
    fun `Single library`() {
        createRepo {
            file("Jenkinsfile", """
                @Library('pipeline-general@1.0.1') _
                
                pipeline()
            """.trimIndent())
            git("commit", "-m", "Commit of the Jenkinsfile")
        } and { repo, _ ->
            project {
                gitProject(repo)
                val indicators = computer.computeIndicators(project)
                assertEquals(
                    listOf(
                        IndicatorComputedValue(
                            type = IndicatorComputedType(
                                category = IndicatorComputedCategory(
                                    "jenkins-pipeline-library",
                                    "Jenkins pipeline libraries"
                                ),
                                id = "pipeline-general",
                                name = "pipeline-general",
                                link = null,
                                valueType = valueType,
                                valueConfig = JenkinsPipelineLibraryIndicatorValueTypeConfig(false, null),
                            ),
                            value = JenkinsPipelineLibraryVersion("1.0.1"),
                            comment = null
                        ),
                    ),
                    indicators
                )
            }
        }
    }

    @Test
    fun `Several libraries detected`() {
        createRepo {
            file("Jenkinsfile", """
                @Library("pipeline-general@1.0.1")
                @Library("pipeline-common") _
                
                pipeline()
            """.trimIndent())
            git("commit", "-m", "Commit of the Jenkinsfile")
        } and { repo, _ ->
            project {
                gitProject(repo)
                val indicators = computer.computeIndicators(project)
                assertEquals(
                    listOf(
                        IndicatorComputedValue(
                            type = IndicatorComputedType(
                                category = IndicatorComputedCategory(
                                    "jenkins-pipeline-library",
                                    "Jenkins pipeline libraries"
                                ),
                                id = "pipeline-general",
                                name = "pipeline-general",
                                link = null,
                                valueType = valueType,
                                valueConfig = JenkinsPipelineLibraryIndicatorValueTypeConfig(false, null),
                            ),
                            value = JenkinsPipelineLibraryVersion("1.0.1"),
                            comment = null
                        ),
                        IndicatorComputedValue(
                            type = IndicatorComputedType(
                                category = IndicatorComputedCategory(
                                    "jenkins-pipeline-library",
                                    "Jenkins pipeline libraries"
                                ),
                                id = "pipeline-common",
                                name = "pipeline-common",
                                link = null,
                                valueType = valueType,
                                valueConfig = JenkinsPipelineLibraryIndicatorValueTypeConfig(false, null),
                            ),
                            value = null,
                            comment = null
                        ),
                    ),
                    indicators
                )
            }
        }
    }

}