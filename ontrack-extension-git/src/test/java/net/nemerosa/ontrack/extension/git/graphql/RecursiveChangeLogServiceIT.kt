package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RecursiveChangeLogServiceIT: AbstractGitTestSupport() {

    @Autowired
    private lateinit var recursiveChangeLogService: RecursiveChangeLogService

    @Test
    fun `Getting dependency change log with previous build being present`() {
        // Dependency
        val dep = project {
            branch {
                build("dv1")
                build("dv2")
                build("dv3")
            }
        }
        // Source project
        project {
            branch {
                val a = build("a") {
                    linkTo(dep, "dv1")
                }
                val b = build("b") {
                    linkTo(dep, "dv2")
                }
                build("c") {
                    linkTo(dep, "dv2")
                }
                val d = build("d") {
                    linkTo(dep, "dv3")
                }
                // Gets the dependency change from d to b.
                // I expect a change log between dv3 and dv1
                val deps = recursiveChangeLogService.getDependencyChangeLog(d, b, dep.name)
                assertNotNull(deps, "Dependency change is available") { (depFrom, depTo) ->
                    assertEquals("dv3", depFrom.name)
                    assertEquals("dv1", depTo.name)
                }
            }
        }
    }

}