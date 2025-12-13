package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BuildLinkDecorationExtensionIT : AbstractGeneralExtensionTestSupport() {

    @Autowired
    private lateinit var extension: BuildLinkDecorationExtension

    @Test
    fun `No link`() {
        project {
            branch {
                build {
                    val decorations = extension.getDecorations(this)
                    assertEquals(1, decorations.size)
                    val decoration = decorations[0]
                    val list = decoration.data
                    assertEquals(0, list.linksCount)
                }
            }
        }
    }

    @Test
    fun `Recording the number of links`() {
        val ref1 = project<Build> {
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
                    assertEquals(2, list.linksCount)
                }
            }
        }
    }

}