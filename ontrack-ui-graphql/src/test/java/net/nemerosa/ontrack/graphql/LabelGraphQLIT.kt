package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LabelGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Schema OK`() {
        val data = run("""
           {
                labels {
                    category
                    name
                }
           }
        """)
        val labels = data["labels"]
        assertNotNull(labels)
    }

    @Test
    fun `Filtering project list based on labels`() {
        // Creates two labels
        val l1 = label()
        val l2 = label()
        // Project without label
        val p0 = project()
        // Project with one label
        val p1 = project {
            labels = listOf(l1)
        }
        // Project with two labels
        val p2 = project {
            labels = listOf(l1, l2)
        }
        // Looking without any filter on labels
        run("""
            query Projects {
                projects {
                    name
                }
            }
        """).apply {
            val names = path("projects").map { it["name"].textValue() }
            assertTrue(p0.name in names)
            assertTrue(p1.name in names)
            assertTrue(p2.name in names)
        }
        // Filter on one label
        run("""
            query Projects(${'$'}labels: [String!]!) {
                projects(labels: ${'$'}labels) {
                    name
                }
            }
        """, mapOf("labels" to listOf(l1.getDisplay()))).apply {
            val names = path("projects").map { it["name"].textValue() }
            assertFalse(p0.name in names)
            assertTrue(p1.name in names)
            assertTrue(p2.name in names)
        }
        // Filter on two labels
        run("""
            query Projects(${'$'}labels: [String!]!) {
                projects(labels: ${'$'}labels) {
                    name
                }
            }
        """, mapOf("labels" to listOf(l1.getDisplay(), l2.getDisplay()))).apply {
            val names = path("projects").map { it["name"].textValue() }
            assertFalse(p0.name in names)
            assertFalse(p1.name in names)
            assertTrue(p2.name in names)
        }
    }

}