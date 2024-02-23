package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.*
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

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

    @BeforeEach
    fun init() {
        ontrackConfigProperties.templating.errors = OntrackConfigProperties.TemplatingErrors.MESSAGE
    }

    @AfterEach
    fun tearDown() {
        ontrackConfigProperties.templating.errors = OntrackConfigProperties.TemplatingErrors.IGNORE
    }

    @Test
    fun `Getting a plain text change log in a template`() {
        doTestRendering(
            renderer = PlainEventRenderer.INSTANCE,
            template = """
                Version ${'$'}{project} ${'$'}{build} has been released.
                
                ${'$'}{promotionRun.changelog}
            """.trimIndent(),
        ) { _, run, _ ->
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
        ) { _, run, repositoryName ->
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
        ) { _, run, repositoryName ->
            """
                # Version [${run.project.name}](http://localhost:8080/#/project/${run.project.id}) [${run.build.name}](http://localhost:8080/#/build/${run.build.id}) has been released
            
                * [ISS-21](mock://${repositoryName}/issue/ISS-21) Some new feature
                * [ISS-22](mock://${repositoryName}/issue/ISS-22) Some fixes are needed
                * [ISS-23](mock://${repositoryName}/issue/ISS-23) Some nicer UI
            """.trimIndent()
        }
    }

    @Test
    fun `Getting a Markdown change log in a template with a title`() {
        doTestRendering(
            renderer = markdownEventRenderer,
            template = """
                # Version ${'$'}{project} ${'$'}{build} has been released
                
                ${'$'}{promotionRun.changelog?title=true}
            """.trimIndent(),
        ) { fromBuild, run, repositoryName ->
            """
                # Version [${run.project.name}](http://localhost:8080/#/project/${run.project.id}) [${run.build.name}](http://localhost:8080/#/build/${run.build.id}) has been released
                
                ## Change log for [${run.project.name}](http://localhost:8080/#/project/${run.project.id}) from [${fromBuild.name}](http://localhost:8080/#/build/${fromBuild.id}) to [${run.build.name}](http://localhost:8080/#/build/${run.build.id})
            
                * [ISS-21](mock://${repositoryName}/issue/ISS-21) Some new feature
                * [ISS-22](mock://${repositoryName}/issue/ISS-22) Some fixes are needed
                * [ISS-23](mock://${repositoryName}/issue/ISS-23) Some nicer UI
            """.trimIndent()
        }
    }

    @Test
    fun `Getting a recursive change log in a template`() {
        prepareTest { fromBuild, run, _ ->
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
                            ${'$'}{promotionRun.changelog?dependencies=${run.project.name}}
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

    @Test
    fun `Getting a recursive change log in a template using qualifiers`() {
        prepareTest { fromBuild, run, _ ->
            project {
                branch {
                    val pl = promotionLevel()

                    build {
                        linkTo(fromBuild, qualifier = "sub")
                        promote(pl)
                    }

                    build {
                        linkTo(run.build, qualifier = "sub")
                        val parentRun = promote(pl)

                        val event = eventFactory.newPromotionRun(parentRun)

                        // Template
                        val template = """
                            ${'$'}{promotionRun.changelog?dependencies=${run.project.name}:sub}
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

    @Test
    fun `Getting a recursive change log in a template using a title`() {
        prepareTest { fromBuild, run, _ ->
            project {
                branch {
                    val pl = promotionLevel()

                    build {
                        linkTo(fromBuild, qualifier = "sub")
                        promote(pl)
                    }

                    build {
                        linkTo(run.build, qualifier = "sub")
                        val parentRun = promote(pl)

                        val event = eventFactory.newPromotionRun(parentRun)

                        // Template
                        val template = """
                            ${'$'}{promotionRun.changelog?title=true&dependencies=${run.project.name}:sub}
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
                                Change log for ${fromBuild.project.name} from ${fromBuild.name} to ${run.build.name}
                                
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

    @Test
    fun `Getting a recursive change log in a template using a title when there is no change`() {
        prepareTest { fromBuild, run, _ ->
            project {
                branch {
                    val pl = promotionLevel()

                    build {
                        linkTo(fromBuild, qualifier = "sub")
                        promote(pl)
                    }

                    build {
                        linkTo(fromBuild, qualifier = "sub") // Targeting the same build --> no change
                        val parentRun = promote(pl)

                        val event = eventFactory.newPromotionRun(parentRun)

                        // Template
                        val template = """
                            ${'$'}{promotionRun.changelog?title=true&empty=No change&dependencies=${run.project.name}:sub}
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
                                Project ${fromBuild.project.name} version ${fromBuild.name}
                                
                                No change
                            """.trimIndent(),
                            text,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting a deep recursive change log in a template`() {
        prepareTest { fromBuild, run, _ ->
            project {
                val one = this
                branch {
                    val oneFrom = build {
                        linkTo(fromBuild)
                    }
                    val oneTo = build {
                        linkTo(run.build)
                    }

                    project {
                        branch {
                            val pl = promotionLevel()
                            build {
                                linkTo(oneFrom)
                                promote(pl)
                            }
                            build {
                                linkTo(oneTo)
                                val topRun = promote(pl)

                                val event = eventFactory.newPromotionRun(topRun)

                                // Template
                                val template = """
                                    ${'$'}{promotionRun.changelog?dependencies=${one.project.name},${run.project.name}}
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
        }
    }

    @Test
    fun `Getting change log across branches is implemented by default`() {
        doTestAcrossBranches(
            template = """
                ${'$'}{promotionRun.changelog}
            """.trimIndent(),
            expectedText = """
                * ISS-21 Some new feature
                * ISS-22 Some fixes are needed
                * ISS-23 Some nicer UI
            """.trimIndent(),
        )
    }

    @Test
    fun `Getting change log across branches can be disabled`() {
        doTestAcrossBranches(
            template = """
                ${'$'}{promotionRun.changelog?acrossBranches=false}
            """.trimIndent(),
            expectedText = "",
        )
    }

    @Test
    fun `Empty change log can have a default value if not available`() {
        doTestAcrossBranches(
            template = """
                ${'$'}{promotionRun.changelog?acrossBranches=false&empty=No change log is available.}
            """.trimIndent(),
            expectedText = "No change log is available.",
        )
    }

    private fun doTestAcrossBranches(
        template: String,
        expectedText: String,
    ) {
        asAdmin {
            mockSCMTester.withMockSCMRepository {
                project {
                    val plName = uid("pl-")
                    branch {
                        configureMockSCMBranch("release/1.26")
                        val pl = promotionLevel(plName)
                        build {
                            // Mock termination commit
                            repositoryIssue("ISS-20", "Last issue before the change log")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                            // Promotion boundary
                            promote(pl)
                        }
                        // Additional builds since the promotion
                        build {
                            repositoryIssue("ISS-21", "Some new feature")
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                    }
                    branch {
                        configureMockSCMBranch("release/1.27")
                        val pl = promotionLevel(plName)
                        build {
                            repositoryIssue("ISS-22", "Some fixes are needed")
                            withRepositoryCommit("ISS-22 Fixing some bugs")
                        }
                        build {
                            repositoryIssue("ISS-23", "Some nicer UI")
                            withRepositoryCommit("ISS-23 Fixing some CSS")

                            // Promotion boundary
                            val run = promote(pl)
                            val event = eventFactory.newPromotionRun(run)

                            // Rendering
                            val text = eventTemplatingService.render(
                                template = template,
                                event = event,
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            // OK
                            assertEquals(
                                expectedText,
                                text,
                            )
                        }
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
        expectedText: (fromBuild: Build, run: PromotionRun, repositoryName: String) -> String,
    ) {
        prepareTest { fromBuild, run, repositoryName ->
            val event = eventFactory.newPromotionRun(run)

            // Rendering
            val text = eventTemplatingService.render(
                template = template,
                event = event,
                renderer = renderer
            )

            // OK
            val expectedLines = expectedText(fromBuild, run, repositoryName).lines().map { it.trim() }
            val actualLines = text.lines().map { it.trim() }
            assertEquals(
                expectedLines,
                actualLines,
            )
        }
    }

}