package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.structure.Build

/**
 * Creates the following dependency structure:
 *
 * ```
 * p --> q1(L) --> r1(L)
 *   |         |-> r2 --> s1
 *   |                |-> s2(L)
 *   |-> q3 --> r3
 *          |-> r4(L)
 *          |-> r5
 * ```
 *
 * when builds with `(L)` have a project being
 * labelled.
 */
fun AbstractDSLTestSupport.forRecursiveLinks(code: (label: Label, p: Build, builds: Map<String, Build>) -> Unit) {
    asAdmin {
        val label = label()

        val s1 = doCreateBuild()
        val s2 = doCreateBuild().apply {
            project.labels = listOf(label)
        }

        val r1 = doCreateBuild().apply {
            project.labels = listOf(label)
        }
        val r2 = doCreateBuild().apply {
            linkTo(s1)
            linkTo(s2)
        }
        val r3 = doCreateBuild()
        val r4 = doCreateBuild().apply {
            project.labels = listOf(label)
        }
        val r5 = doCreateBuild()

        val q1 = doCreateBuild().apply {
            project.labels = listOf(label)
            linkTo(r1)
            linkTo(r2)
        }
        val q2 = doCreateBuild().apply {
            linkTo(r3)
            linkTo(r4)
            linkTo(r5)
        }

        val p = doCreateBuild().apply {
            linkTo(q1)
            linkTo(q2)
        }

        val builds = mapOf(
            "p" to p,
            "q1" to q1,
            "q2" to q2,
            "r1" to r1,
            "r2" to r2,
            "r3" to r3,
            "r4" to r4,
            "r5" to r5,
            "s1" to s1,
            "s2" to s2,
        )

        code(label, p, builds)

    }
}