package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BuildLinkDecorationExtensionIT : AbstractGeneralExtensionTestSupport() {

    @Autowired
    private lateinit var extension: BuildLinkDecorationExtension

    @Test
    fun `No link`() {
        project {
            branch {
                build {
                    val decorations = extension.getDecorations(this)
                    assertTrue(decorations.isEmpty(), "No decoration")
                }
            }
        }
    }

    @Test
    fun `Only main links`() {
        val labelCategory = uid("c")
        val labelName = uid("n")
        withMainBuildLinksSettings {
            setMainBuildLinksSettings("$labelCategory:$labelName")
            val ref1 = project<Build> {
                labels = listOf(label(labelCategory, labelName), label("something", "else"))
                branch<Build> {
                    build()
                }
            }
            val ref2 = project<Build> {
                labels = listOf(label(labelCategory, labelName))
                branch<Build> {
                    build()
                }
            }
            project {
                branch {
                    build {
                        linkTo(ref1)
                        linkTo(ref2)
                        val decorations = extension.getDecorations(this)
                        assertEquals(1, decorations.size)
                        val decoration = decorations[0]
                        val list = decoration.data
                        assertNull(list.extraLink, "No extra link")
                        assertEquals(
                                setOf(
                                        ref1.name,
                                        ref2.name
                                ),
                                list.decorations.map { it.build }.toSet()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Main links and extra links`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings("type:product")
            val ref1 = project<Build> {
                labels = listOf(label("type", "product"), label("something", "else"))
                branch<Build> {
                    build()
                }
            }
            val ref2 = project<Build> {
                labels = listOf(label("something", "else"))
                branch<Build> {
                    build()
                }
            }
            project {
                branch {
                    build {
                        linkTo(ref1)
                        linkTo(ref2)
                        val decorations = extension.getDecorations(this)
                        assertEquals(1, decorations.size)
                        val decoration = decorations[0]
                        val list = decoration.data
                        assertEquals("urn:test:#:entity:BUILD:$id", list.extraLink?.toString(), "Extra link")
                        assertEquals(
                                setOf(
                                        ref1.name
                                ),
                                list.decorations.map { it.build }.toSet()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `No main link`() {
        withMainBuildLinksSettings {
            setMainBuildLinksSettings("type:product")
            val ref1 = project<Build> {
                labels = listOf(label("something", "else"))
                branch<Build> {
                    build()
                }
            }
            val ref2 = project<Build> {
                branch<Build> {
                    build()
                }
            }
            project {
                branch {
                    build {
                        linkTo(ref1)
                        linkTo(ref2)
                        val decorations = extension.getDecorations(this)
                        assertEquals(1, decorations.size)
                        val decoration = decorations[0]
                        val list = decoration.data
                        assertEquals("urn:test:#:entity:BUILD:$id", list.extraLink?.toString(), "Extra link")
                        assertTrue(list.decorations.isEmpty(), "No main link")
                    }
                }
            }
        }
    }

}