package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

/**
 * In this test, we assume the source project is part of the main labels (see [MainBuildLinksProjectPropertyType])
 * and that it is always displayed.
 */
class BuildLinkDecorationIT : AbstractDSLTestSupport() {

    private val targetBuildName = "2"
    private val targetLabel = "v2.0.0"

    @Autowired
    private lateinit var buildLinkDecorationExtension: BuildLinkDecorationExtension

    @Test
    fun `Build link decoration using build name when not having label when project not configured`() {
        testBuildLinkDisplayOptions(
                useLabel = null,
                label = null,
                expectedLabel = targetBuildName
        )
    }

    @Test
    fun `Build link decoration using build name when having label when project not configured`() {
        testBuildLinkDisplayOptions(
                useLabel = null,
                label = targetLabel,
                expectedLabel = targetBuildName
        )
    }

    @Test
    fun `Build link decoration using build name when having label when project configured to not use label`() {
        testBuildLinkDisplayOptions(
                useLabel = false,
                label = targetLabel,
                expectedLabel = targetBuildName
        )
    }

    @Test
    fun `Build link decoration using build name when not having label when project configured to not use label`() {
        testBuildLinkDisplayOptions(
                useLabel = false,
                label = null,
                expectedLabel = targetBuildName
        )
    }

    @Test
    fun `Build link decoration using label when having label when project configured to use label`() {
        testBuildLinkDisplayOptions(
                useLabel = true,
                label = targetLabel,
                expectedLabel = targetLabel
        )
    }

    @Test
    fun `Build link decoration using build name when not having label when project configured to use label`() {
        testBuildLinkDisplayOptions(
                useLabel = true,
                label = null,
                expectedLabel = targetBuildName
        )
    }

    private fun testBuildLinkDisplayOptions(
            useLabel: Boolean?,
            label: String?,
            expectedLabel: String
    ) {
        val projectLabel = uid("L")
        withMainBuildLinksSettings {
            setMainBuildLinksSettings(projectLabel)
            project target@{
                labels = listOf(label(category = null, name = projectLabel))
                if (useLabel != null) {
                    buildLinkDisplaysOptions(useLabel)
                }
                branch {
                    build("2") {
                        if (label != null) {
                            label(label)
                        }
                    }
                    project {
                        branch {
                            build("1") {
                                linkTo(this@target, "2")
                                checkLabel(expectedLabel)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Project.buildLinkDisplaysOptions(useLabel: Boolean) {
        setProperty(
                this,
                BuildLinkDisplayPropertyType::class.java,
                BuildLinkDisplayProperty(useLabel)
        )
    }

    private fun Build.label(label: String) {
        setProperty(
                this,
                ReleasePropertyType::class.java,
                ReleaseProperty(label)
        )
    }

    private fun Build.checkLabel(expectedLabel: String) {
        val decorations = buildLinkDecorationExtension.getDecorations(this)
        assertEquals(1, decorations.size)
        val mainDecorations = decorations[0].data.decorations
        assertEquals(1, mainDecorations.size)
        val decoration = mainDecorations[0]
        assertEquals(expectedLabel, decoration.label, "Build $name decoration's label is expected to be $expectedLabel")
    }

}