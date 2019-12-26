package net.nemerosa.ontrack.extension.scm.catalog.contributors

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMLocation
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SCMLocationCatalogInfoContributorTest {

    private lateinit var contributor: SCMLocationCatalogInfoContributor
    private lateinit var provider: SCMCatalogProvider
    private val entry = CatalogFixtures.entry()
    private val project: Project = Project.of(NameDescription.nd("PRJ", "")).withId(ID.of(1))

    @Before
    fun setup() {
        provider = mock()
        contributor = SCMLocationCatalogInfoContributor(
                SCMExtensionFeature(),
                listOf(provider)
        )
    }

    @Test
    fun `Not returning info if no match`() {
        whenever(provider.matches(entry, project)).thenReturn(false)
        val location = contributor.collectInfo(project, entry)
        assertNull(location)
    }

    @Test
    fun `Returning null info if null`() {
        whenever(provider.matches(entry, project)).thenReturn(true)
        whenever(provider.getSCMLocation(project)).thenReturn(null)
        val location = contributor.collectInfo(project, entry)
        assertNull(location)
    }

    @Test
    fun `Returning info`() {
        whenever(provider.matches(entry, project)).thenReturn(true)
        whenever(provider.getSCMLocation(project)).thenReturn(location())
        val location = contributor.collectInfo(project, entry)
        assertEquals(location(), location)
    }

    @Test
    fun `Location to JSON`() {
        assertEquals(
                mapOf(
                        "scm" to "git",
                        "name" to "Config",
                        "uri" to "uri",
                        "url" to "url"
                ).asJson(),
                contributor.asJson(location())
        )
    }

    @Test
    fun `JSON to location`() {
        assertEquals(
                location(),
                contributor.fromJson(
                        mapOf(
                                "scm" to "git",
                                "name" to "Config",
                                "uri" to "uri",
                                "url" to "url"
                        ).asJson()
                )
        )
    }

    private fun location() = SCMLocation(
            scm = "git",
            name = "Config",
            uri = "uri",
            url = "url"
    )

    @Test
    fun `Delegating collection to SCM providers`() {
    }

}