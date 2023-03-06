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
                build("a") {
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
                // I expect a change log between dv3 and dv2
                val deps = recursiveChangeLogService.getDependencyChangeLog(d, b, dep.name)
                assertNotNull(deps, "Dependency change is available") { (depFrom, depTo) ->
                    assertEquals("dv3", depFrom.name)
                    assertEquals("dv2", depTo.name)
                }
            }
        }
    }

    @Test
    fun `Getting dependency change log for one build`() {
        // Dependency
        val dep = project {
            branch {
                (0..11).forEach {
                    build("4.4.$it")
                }
            }
        }
        // Source project
        project {
            branch {
                val b40 = build("40") {
                    linkTo(dep, "4.4.4")
                }
                val b41 = build("41") {
                    linkTo(dep, "4.4.5")
                }
                val b42 = build("42") {
                    linkTo(dep, "4.4.11")
                }
                // Getting a change log for b42, we get the change log since b41
                // so we expect a dependendy change log to be between 4.4.11 and 4.4.5
                val deps = recursiveChangeLogService.getDependencyChangeLog(b42, b41, dep.name)
                assertNotNull(deps, "Dependency change is available") { (depFrom, depTo) ->
                    assertEquals("4.4.11", depFrom.name)
                    assertEquals("4.4.5", depTo.name)
                }
            }
        }
    }

}