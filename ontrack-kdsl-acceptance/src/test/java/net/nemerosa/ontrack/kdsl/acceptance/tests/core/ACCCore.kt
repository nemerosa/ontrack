package net.nemerosa.ontrack.kdsl.acceptance.tests.core

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ACCCore : AbstractACCDSLTestSupport() {

    @Test
    fun `Branch not found before not authorised`() {
        project {
            branch {
                withNotGrantProjectViewToAll {
                    withUser {
                        assertNull(
                            ontrack.findBranchByName(project.name, name)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Project default description is empty`() {
        project {
            branch {
                assertEquals("", project.description, "Project description is empty by default")
                assertEquals("", this.description, "Branch description is empty by default")
            }
        }
    }

    @Test
    fun `List of projects`() {
        project {
            val list = ontrack.projects()
            assertNotNull(list.find { it.id == id }, "Project found")
        }
    }

    @Test
    fun `Finding a project by name`() {
        val xName = uid("p-")
        project {
            assertNotNull(ontrack.findProjectByName(name)) {
                assertEquals(id, it.id)
            }
            assertNull(ontrack.findProjectByName(xName))
        }
    }

    @Test
    fun `Project branches`() {
        // Project and two branches
        project {
            (1..5).forEach { branch("B$it") {} }
            assertEquals(
                (5 downTo 1).map { "B$it" },
                this.branchList().map { it.name }
            )
        }
    }

}