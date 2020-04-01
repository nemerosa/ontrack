package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URI
import kotlin.test.assertEquals

class MetaInfoSearchExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var extension: MetaInfoSearchExtension

    @Test
    fun `Searching on meta property - found one build`() {
        // Creates a build
        val build = doCreateBuild()
        // Meta info on the build
        setProperty(build, MetaInfoPropertyType::class.java, MetaInfoProperty(listOf(
                MetaInfoPropertyItem.of("name", "value")
        )))
        // Searching
        val results = asUser().withView(build).call {
            extension.searchProvider.search("name:val*")
        }.toList()
        assertEquals(1, results.size)
        results[0].apply {
            assertEquals(build.entityDisplayName, title)
            assertEquals("name -> value", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-meta-info", type.id)
            assertEquals("Build with Meta Info", type.name)
        }
    }

    @Test
    fun `Searching on meta property - found two builds`() {
        // Context
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1", "Build 1"))
        val build2 = doCreateBuild(branch, nd("2", "Build 2"))
        // Meta info on the builds
        listOf(build1, build2).forEachIndexed { index, build ->
            setProperty(build, MetaInfoPropertyType::class.java, MetaInfoProperty(listOf(
                    MetaInfoPropertyItem.of("name", "value${index + 1}")
            )))
        }
        // Searching
        val results = asUser().withView(branch).call {
            extension.searchProvider.search("name:val*")
        }.toList()
        assertEquals(2, results.size)
        results[0].apply {
            assertEquals(build2.entityDisplayName, title)
            assertEquals("name -> value2", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build2.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build2.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-meta-info", type.id)
            assertEquals("Build with Meta Info", type.name)
        }
        results[1].apply {
            assertEquals(build1.entityDisplayName, title)
            assertEquals("name -> value1", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build1.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build1.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-meta-info", type.id)
            assertEquals("Build with Meta Info", type.name)
        }
    }

    @Test
    fun `Searching on meta property - found one build among two ones`() {
        // Context
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1", "Build 1"))
        val build2 = doCreateBuild(branch, nd("2", "Build 2"))
        // Meta info on the builds
        listOf(build1, build2).forEachIndexed { index, build ->
            setProperty(build, MetaInfoPropertyType::class.java, MetaInfoProperty(listOf(
                    MetaInfoPropertyItem.of("name", "value${index + 1}")
            )))
        }
        // Searching
        val results = asUser().withView(branch).call {
            extension.searchProvider.search("name:value1*")
        }.toList()
        results[0].apply {
            assertEquals(build1.entityDisplayName, title)
            assertEquals("name -> value1", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build1.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build1.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-meta-info", type.id)
            assertEquals("Build with Meta Info", type.name)
        }
    }

}
