package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.LinkChangeService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LinkChangeServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var linkChangeService: LinkChangeService

    @Test
    fun `Collection link changes`() {

        val depL = project {
            branch {
                build("L1")
                build("L2")
                build("L3")
            }
        }

        val depM = project {
            branch {
                build("M1")
            }
        }

        val depN = project {
            branch {
                build("N1")
            }
        }

        val depP = project {
            branch {
                build("P1")
            }
        }

        project {
            branch {
                val a = build()
                val b = build()

                a.linkTo(depL, "L1")
                b.linkTo(depL, "L2")

                a.linkTo(depL, "L2", "qualifier")
                b.linkTo(depL, "L3", "qualifier")

                a.linkTo(depM, "M1")
                b.linkTo(depM, "M1")

                a.linkTo(depN, "N1")

                b.linkTo(depP, "P1")

                val changes = linkChangeService.linkChanges(a, b)

                val index = changes.associate {
                    (it.project.name to it.qualifier) to (it.from?.name to it.to?.name)
                }

                assertEquals(
                    mapOf(
                        (depL.name to "") to ("L1" to "L2"),
                        (depL.name to "qualifier") to ("L2" to "L3"),
                        (depM.name to "") to ("M1" to "M1"),
                        (depN.name to "") to ("N1" to null),
                        (depP.name to "") to (null to "P1"),
                    ),
                    index,
                )
            }
        }
    }

}