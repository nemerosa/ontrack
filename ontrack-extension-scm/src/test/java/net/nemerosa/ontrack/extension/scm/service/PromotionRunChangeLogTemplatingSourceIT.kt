package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.*
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

// TODO useProject config parameter to get change logs across branches

class PromotionRunChangeLogTemplatingSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Autowired
    private lateinit var htmlNotificationEventRenderer: HtmlNotificationEventRenderer

    @Autowired
    private lateinit var markdownEventRenderer: MarkdownEventRenderer

    @Test
    fun `Getting a plain text change log in a template`() {
        doTestRendering(
            renderer = PlainEventRenderer.INSTANCE,
            template = """
                Version ${'$'}{project} ${'$'}{build} has been released.
                
                ${'$'}{promotionRun.changelog}
            """.trimIndent(),
        ) { run, _ ->
            """
                Version ${run.project.name} ${run.build.name} has been released.
                
                * ISS-21 Some new feature
                * ISS-22 Some fixes are needed
                * ISS-23 Some nicer UI
            """.trimIndent()
        }
    }

    @Test
    fun `Getting a HTML change log in a template`() {
        doTestRendering(
            renderer = htmlNotificationEventRenderer,
            template = """
                <h3>Version ${'$'}{project} ${'$'}{build} has been released</h3>
                
                ${'$'}{promotionRun.changelog}
            """.trimIndent(),
        ) { run, repositoryName ->
            """
                <h3>Version <a href="http://localhost:8080/#/project/${run.project.id}">${run.project.name}</a> <a href="http://localhost:8080/#/build/${run.build.id}">${run.build.name}</a> has been released</h3>
                
                <ul>
                    <li><a href="mock://${repositoryName}/issue/ISS-21">ISS-21</a> Some new feature</li>
                    <li><a href="mock://${repositoryName}/issue/ISS-22">ISS-22</a> Some fixes are needed</li>
                    <li><a href="mock://${repositoryName}/issue/ISS-23">ISS-23</a> Some nicer UI</li>
                </ul>
            """.trimIndent()
        }
    }

    @Test
    fun `Getting a Markdown change log in a template`() {
        doTestRendering(
            renderer = markdownEventRenderer,
            template = """
                # Version ${'$'}{project} ${'$'}{build} has been released
                
                ${'$'}{promotionRun.changelog}
            """.trimIndent(),
        ) { run, repositoryName ->
            """
                # Version [${run.project.name}](http://localhost:8080/#/project/${run.project.id}) [${run.build.name}](http://localhost:8080/#/build/${run.build.id}) has been released
            
                * [ISS-21](mock://${repositoryName}/issue/ISS-21) Some new feature
                * [ISS-22](mock://${repositoryName}/issue/ISS-22) Some fixes are needed
                * [ISS-23](mock://${repositoryName}/issue/ISS-23) Some nicer UI
            """.trimIndent()
        }
    }

    @Test
    fun `Getting a recursive change log in a template`() {
        prepareTest { fromBuild, run, repositoryName ->
            project {
                branch {
                    val pl = promotionLevel()

                    build {
                        linkTo(fromBuild)
                        promote(pl)
                    }

                    build {
                        linkTo(run.build)
                        val parentRun = promote(pl)

                        val event = eventFactory.newPromotionRun(parentRun)

                        // Template
                        val template = """
                            ${'$'}{promotionRun.changelog?project=${run.project.name}}
                        """.trimIndent()

                        // Rendering
                        val text = eventTemplatingService.render(
                            template = template,
                            event = event,
                            renderer = PlainEventRenderer.INSTANCE
                        )

                        // OK
                        assertEquals(
                            """
                                Version ${run.project.name} ${run.build.name} has been released.
                                
                                * ISS-21 Some new feature
                                * ISS-22 Some fixes are needed
                                * ISS-23 Some nicer UI
                            """.trimIndent(),
                            text,
                        )
                    }
                }
            }
        }
    }

    private fun prepareTest(
        code: (
            fromBuild: Build,
            run: PromotionRun,
            repositoryName: String,
        ) -> Unit,
    ) {
        asAdmin {
            mockSCMTester.withMockSCMRepository {
                project {
                    branch {
                        configureMockSCMBranch()

                        val pl = promotionLevel()

                        build {}
                        val from = build {
                            // Mock termination commit
                            repositoryIssue("ISS-20", "Last issue before the change log")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                            // Promotion boundary
                            promote(pl)
                        }
                        build {
                            repositoryIssue("ISS-21", "Some new feature")
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                        build {
                            repositoryIssue("ISS-22", "Some fixes are needed")
                            withRepositoryCommit("ISS-22 Fixing some bugs")
                        }
                        build {
                            repositoryIssue("ISS-23", "Some nicer UI")
                            withRepositoryCommit("ISS-23 Fixing some CSS")

                            // Promotion boundary
                            val run = promote(pl)

                            code(from, run, repositoryName)
                        }
                    }
                }
            }
        }
    }

    fun doTestRendering(
        renderer: EventRenderer,
        template: String,
        expectedText: (run: PromotionRun, repositoryName: String) -> String,
    ) {
        prepareTest { _, run, repositoryName ->
            val event = eventFactory.newPromotionRun(run)

            // Rendering
            val text = eventTemplatingService.render(
                template = template,
                event = event,
                renderer = renderer
            )

            // OK
            val expectedLines = expectedText(run, repositoryName).lines().map { it.trim() }
            val actualLines = text.lines().map { it.trim() }
            assertEquals(
                expectedLines,
                actualLines,
            )
        }
    }

}