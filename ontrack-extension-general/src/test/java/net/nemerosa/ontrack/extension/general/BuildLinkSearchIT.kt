package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.BuildConfig
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URI
import kotlin.test.assertEquals

class BuildLinkSearchIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var extension: BuildLinkSearchExtension

    @Test
    fun `Searching on build link - found one build`() {
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        val targetPrefix = target.name.substring(0..5)
        // Build link on the build
        asUser().with(build, BuildConfig::class.java).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // Searching
        val results = build.asUserWithView {
            extension.searchProvider.search("${target.project.name}:${targetPrefix}*")
        }.toList()
        assertEquals(1, results.size)
        results[0].apply {
            assertEquals(build.entityDisplayName, title)
            assertEquals("${build.project.name} -> ${build.name}", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-link", type.id)
            assertEquals("Linked Build", type.name)
        }
    }

    /**
     * <pre>
     *     build1 -> target
     *     build2 -> target
     * </pre>
     *
     * Looking for target brings build1 & build2
     */
    @Test
    fun `Searching on build link - found two builds`() {
        // Creates a target build
        val target = doCreateBuild()
        val targetPrefix = target.name.substring(0..5)
        // Two builds to find
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1.1", "Build 1"))
        val build2 = doCreateBuild(branch, nd("1.2", "Build 2"))
        // Meta info on the build
        listOf(build1, build2).forEach { build ->
            asUser().with(build, BuildConfig::class.java).withView(target).call {
                structureService.addBuildLink(build, target)
            }
        }
        // Searching
        val results = asUser().withView(target).withView(branch).call {
            extension.searchProvider.search("${target.project.name}:${targetPrefix}*")
        }.toList()
        assertEquals(2, results.size)
        results[0].apply {
            assertEquals(build2.entityDisplayName, title)
            assertEquals("${build2.project.name} -> ${build2.name}", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build2.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build2.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-link", type.id)
            assertEquals("Linked Build", type.name)
        }
        results[1].apply {
            assertEquals(build1.entityDisplayName, title)
            assertEquals("${build1.project.name} -> ${build1.name}", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build1.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build1.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-link", type.id)
            assertEquals("Linked Build", type.name)
        }
    }

    @Test
    fun `Searching on build link - found one build among two ones`() {
        // Creates two target builds
        val targetBranch = doCreateBranch()
        val target1 = doCreateBuild(targetBranch, nd("1.0", ""))
        val target2 = doCreateBuild(targetBranch, nd("2.0", ""))
        // Context
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1", "Build 1"))
        val build2 = doCreateBuild(branch, nd("2", "Build 2"))
        // Meta info on the builds
        asUser().with(branch, BuildConfig::class.java).withView(targetBranch).call {
            structureService.addBuildLink(build1, target1)
            structureService.addBuildLink(build2, target2)
        }
        // Searching
        val results = asUser().withView(branch).withView(targetBranch).call {
            extension.searchProvider.search("${targetBranch.project.name}:1*")
        }.toList()
        results[0].apply {
            assertEquals(build1.entityDisplayName, title)
            assertEquals("${build1.project.name} -> ${build1.name}", description)
            assertEquals(URI.create("urn:test:entity:BUILD:${build1.id}"), uri)
            assertEquals(URI.create("urn:test:#:entity:BUILD:${build1.id}"), page)
            assertEquals(100.0, accuracy)
            assertEquals("general", type.feature.id)
            assertEquals("build-link", type.id)
            assertEquals("Linked Build", type.name)
        }
    }

}
