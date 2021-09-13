package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.indicators.computing.*
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JenkinsPipelineFileIndicatorComputerIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var computer: JenkinsPipelineFileIndicatorComputer

    @Autowired
    private lateinit var booleanIndicatorValueType: BooleanIndicatorValueType

    @Autowired
    private lateinit var configurableIndicatorService: ConfigurableIndicatorService

    @Test
    fun `Project not eligible if no Git configuration`() {
        project {
            assertFalse(computer.isProjectEligible(this), "Project not eligible if no Git configuration")
        }
    }

    @Test
    fun `No indicator value when no Git configuration, even when indicator is enabled`() {
        project {
            withIndicatorEnabled(required = true) {
                val indicators = computer.computeIndicators(project)
                assertEquals(1, indicators.size)
                assertNull(indicators.first().value, "No value")
            }
        }
    }

    @Test
    fun `Jenkinsfile is present but indicator is not enabled`() {
        createRepo {
            file(
                "Jenkinsfile", """
                @Library('pipeline-general@1.0.1') _
                
                pipeline()
            """.trimIndent()
            )
            git("commit", "-m", "Commit of the Jenkinsfile")
        } and { repo, _ ->
            project {
                gitProject(repo)
                withIndicatorDisabled {
                    val indicators = computer.computeIndicators(project)
                    assertTrue(indicators.isEmpty(), "No indicator since not enabled")
                }
            }
        }
    }

    @Test
    fun `Jenkinsfile is present and required`() {
        createRepo {
            file(
                "Jenkinsfile", """
                @Library('pipeline-general@1.0.1') _
                
                pipeline()
            """.trimIndent()
            )
            git("commit", "-m", "Commit of the Jenkinsfile")
        } and { repo, _ ->
            project {
                gitProject(repo)
                withIndicatorEnabled(required = true) {
                    val indicators = computer.computeIndicators(project)
                    assertEquals(
                        listOf(
                            IndicatorComputedValue(
                                type = IndicatorComputedType(
                                    category = IndicatorComputedCategory(
                                        "jenkins-pipeline-file",
                                        "Jenkins pipeline file"
                                    ),
                                    id = "jenkins-pipeline-file",
                                    name = "The repository MUST have a Jenkinsfile",
                                    link = null,
                                    valueType = booleanIndicatorValueType,
                                    valueConfig = BooleanIndicatorValueTypeConfig(required = true),
                                ),
                                value = true,
                                comment = null
                            ),
                        ),
                        indicators
                    )
                }
            }
        }
    }

    @Test
    fun `Jenkinsfile is present and optional`() {
        createRepo {
            file(
                "Jenkinsfile", """
                @Library('pipeline-general@1.0.1') _
                
                pipeline()
            """.trimIndent()
            )
            git("commit", "-m", "Commit of the Jenkinsfile")
        } and { repo, _ ->
            project {
                gitProject(repo)
                withIndicatorEnabled(required = false) {
                    val indicators = computer.computeIndicators(project)
                    assertEquals(
                        listOf(
                            IndicatorComputedValue(
                                type = IndicatorComputedType(
                                    category = IndicatorComputedCategory(
                                        "jenkins-pipeline-file",
                                        "Jenkins pipeline file"
                                    ),
                                    id = "jenkins-pipeline-file",
                                    name = "The repository SHOULD have a Jenkinsfile",
                                    link = null,
                                    valueType = booleanIndicatorValueType,
                                    valueConfig = BooleanIndicatorValueTypeConfig(required = false),
                                ),
                                value = true,
                                comment = null
                            ),
                        ),
                        indicators
                    )
                }
            }
        }
    }

    private fun withIndicatorDisabled(code: () -> Unit) {
        withIndicator(enabled = false, required = false, code)
    }

    private fun withIndicatorEnabled(required: Boolean = false, code: () -> Unit) {
        withIndicator(enabled = true, required = required, code)
    }

    private fun withIndicator(enabled: Boolean, required: Boolean, code: () -> Unit) {
        val type = computer.configurableIndicators.first()
        configurableIndicatorService.saveConfigurableIndicator(
            type,
            ConfigurableIndicatorState(
                enabled = enabled,
                link = null,
                values = listOf(
                    ConfigurableIndicatorAttributeValue(
                        attribute = ConfigurableIndicatorAttribute.requiredFlag,
                        value = required.toString()
                    )
                )
            )
        )
        code()
    }

}