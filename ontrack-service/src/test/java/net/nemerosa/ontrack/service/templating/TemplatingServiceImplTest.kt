package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.templating.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TemplatingServiceImplTest {

    private lateinit var templatingService: TemplatingService
    private lateinit var ontrackConfigProperties: OntrackConfigProperties

    @BeforeEach
    fun init() {

        ontrackConfigProperties = OntrackConfigProperties()
        ontrackConfigProperties.templating.errors = OntrackConfigProperties.TemplatingErrors.MESSAGE

        val scmBranchSource = object : AbstractTemplatingSource(
            field = "scmBranch",
            type = ProjectEntityType.BRANCH,
        ) {
            override fun render(
                entity: ProjectEntity,
                configMap: Map<String, String>,
                renderer: EventRenderer
            ): String =
                "feature/${entity.displayName}"
        }

        val listProjectSource = object : AbstractTemplatingSource(
            field = "list",
            type = ProjectEntityType.PROJECT
        ) {
            override fun render(
                entity: ProjectEntity,
                configMap: Map<String, String>,
                renderer: EventRenderer
            ): String {
                val list = configMap.getListStringsTemplatingParam("projects") ?: emptyList()
                return renderer.renderList(list)
            }
        }

        val repositorySource = object : AbstractTemplatingSource(
            field = "repository",
            type = ProjectEntityType.PROJECT
        ) {
            override fun render(
                entity: ProjectEntity,
                configMap: Map<String, String>,
                renderer: EventRenderer
            ): String {
                val name = configMap.getRequiredTemplatingParam("name")
                val organization = configMap.getRequiredTemplatingParam("org")
                return "https://github.com/$organization/$name"
            }
        }

        val templatingSources = listOf(
            scmBranchSource,
            listProjectSource,
            repositorySource,
        )

        val uppercaseTemplatingFilter = object : TemplatingFilter {
            override val id: String = "uppercase"
            override fun apply(text: String): String = text.uppercase()
        }

        val templatingFilters = listOf<TemplatingFilter>(
            uppercaseTemplatingFilter,
        )

        val templatingFunctions = listOf<TemplatingFunction>(
            LinkTemplatingFunction(),
        )

        templatingService = TemplatingServiceImpl(
            templatingSources = templatingSources,
            templatingFilters = templatingFilters,
            templatingFunctions = templatingFunctions,
            ontrackConfigProperties = ontrackConfigProperties,
        )
    }

    @Test
    fun `Checking the legacy templates`() {
        assertFalse(templatingService.isLegacyTemplate("No expression at all."), "Plain text")
        assertTrue(templatingService.isLegacyTemplate("Getting a {branch} name the old way."), "Legacy")
        assertTrue(
            templatingService.isLegacyTemplate("Getting a {branch|uppercase} name the old way."),
            "Legacy with filter"
        )
        assertFalse(templatingService.isLegacyTemplate("Getting a ${'$'}{branch} name the new way."), "New templating")
        assertFalse(
            templatingService.isLegacyTemplate("Getting a ${'$'}{branch} name the new way and left over {branch}."),
            "New templating mixed"
        )
    }

    @Test
    fun `Using list of strings as config parameters for a source`() {
        val project = ProjectFixtures.testProject()
        val text = templatingService.render(
            template = """
                List:
                
                ${'$'}{project.list?projects=one,two,three}
            """.trimIndent(),
            context = mapOf(
                "project" to project,
            ),
            renderer = PlainEventRenderer()
        )
        assertEquals(
            """
                List:
                
                * one
                * two
                * three
            """.trimIndent(),
            text
        )
    }

    @Test
    fun `Rendering entity names`() {
        val branch = BranchFixtures.testBranch()
        val text = templatingService.render(
            template = """
                Branch ${'$'}{branch} has been created
                in project ${'$'}{project}.
            """.trimIndent(),
            context = mapOf(
                "project" to branch.project,
                "branch" to branch,
            ),
            renderer = PlainEventRenderer()
        )
        assertEquals(
            """
                Branch ${branch.name} has been created
                in project ${branch.project.name}.
            """.trimIndent(),
            text
        )
    }

    @Test
    fun `Rendering links for HTML`() {
        val text = templatingService.render(
            template = """
                Link to ${'$'}{#.link?href=LINK_HREF&text=LINK_TEXT}.
            """.trimIndent(),
            context = mapOf(
                "LINK_TEXT" to "Ontrack",
                "LINK_HREF" to "https://nemerosa.github.io/ontrack",
            ),
            renderer = HtmlNotificationEventRenderer(ontrackConfigProperties)
        )
        assertEquals(
            """
                Link to <a href="https://nemerosa.github.io/ontrack">Ontrack</a>.
            """.trimIndent(),
            text
        )
    }

    @Test
    fun `Rendering links for text`() {
        val text = templatingService.render(
            template = """
                Link to ${'$'}{#.link?href=LINK_HREF&text=LINK_TEXT}.
            """.trimIndent(),
            context = mapOf(
                "LINK_TEXT" to "Ontrack",
                "LINK_HREF" to "https://nemerosa.github.io/ontrack",
            ),
            renderer = PlainEventRenderer.INSTANCE
        )
        assertEquals(
            """
                Link to Ontrack.
            """.trimIndent(),
            text
        )
    }

    @Test
    fun `Rendering values`() {
        val text = templatingService.render(
            template = """
                Project ${'$'}{project} has been deleted.
            """.trimIndent(),
            context = mapOf(
                "project" to "PRJ",
            ),
            renderer = PlainEventRenderer()
        )
        assertEquals(
            """
                Project PRJ has been deleted.
            """.trimIndent(),
            text
        )
    }

    @Test
    fun `Rendering with a mix of entities and values`() {
        val project = ProjectFixtures.testProject(name = "ontrack")
        val branch = BranchFixtures.testBranch(project = project, name = "develop")
        val text = templatingService.render(
            template = """
                The ${'$'}{project|uppercase} project has a branch
                which points to a "${'$'}{branch.scmBranch}" SCM branch.
                
                This causes the CVE ${'$'}{cve}.
                
                This would need some action on your side
                to fix the repository at ${'$'}{project.repository?org=nemerosa&name=ontrack}.
            """.trimIndent(),
            context = mapOf(
                "project" to branch.project,
                "branch" to branch,
                "cve" to "CVE-123",
            ),
            renderer = PlainEventRenderer()
        )
        assertEquals(
            """
                The ONTRACK project has a branch
                which points to a "feature/develop" SCM branch.
                
                This causes the CVE CVE-123.
                
                This would need some action on your side
                to fix the repository at https://github.com/nemerosa/ontrack.
            """.trimIndent(),
            text
        )
    }

}