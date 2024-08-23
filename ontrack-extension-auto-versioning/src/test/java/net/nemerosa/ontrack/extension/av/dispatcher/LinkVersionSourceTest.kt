package net.nemerosa.ontrack.extension.av.dispatcher

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinkVersionSourceTest {

    @Test
    fun `Null config`() {
        assertFailsWith<VersionSourceConfigException> {
            LinkVersionSource.parseConfig(null)
        }
    }

    @Test
    fun `Empty config`() {
        assertFailsWith<VersionSourceConfigException> {
            LinkVersionSource.parseConfig("")
        }
    }

    @Test
    fun `Blank config`() {
        assertFailsWith<VersionSourceConfigException> {
            LinkVersionSource.parseConfig(" ")
        }
    }

    @Test
    fun `Wrong config`() {
        assertFailsWith<VersionSourceConfigException> {
            LinkVersionSource.parseConfig("project:qualifier")
        }
    }

    @Test
    fun `Simple project`() {
        val config = LinkVersionSource.parseConfig("project")
        assertEquals(
            LinkVersionSource.LinkVersionSourceConfig(
                project = "project",
                qualifier = "",
                subVersion = null,
            ),
            config
        )
    }

    @Test
    fun `With qualifier`() {
        val config = LinkVersionSource.parseConfig("project/my-qualifier")
        assertEquals(
            LinkVersionSource.LinkVersionSourceConfig(
                project = "project",
                qualifier = "my-qualifier",
                subVersion = null,
            ),
            config
        )
    }

    @Test
    fun `Simple project with sub version`() {
        val config = LinkVersionSource.parseConfig("project->metaInfo/key")
        assertEquals(
            LinkVersionSource.LinkVersionSourceConfig(
                project = "project",
                qualifier = "",
                subVersion = "metaInfo/key",
            ),
            config
        )
    }

    @Test
    fun `Qualifier with sub version`() {
        val config = LinkVersionSource.parseConfig("project/my-qualifier->metaInfo/key")
        assertEquals(
            LinkVersionSource.LinkVersionSourceConfig(
                project = "project",
                qualifier = "my-qualifier",
                subVersion = "metaInfo/key",
            ),
            config
        )
    }

    @Test
    fun `Linked builds`() {
        val config = LinkVersionSource.parseConfig("project->dependency/sample->metaInfo/key")
        assertEquals(
            LinkVersionSource.LinkVersionSourceConfig(
                project = "project",
                qualifier = "",
                subVersion = "dependency/sample->metaInfo/key",
            ),
            config
        )
    }

}