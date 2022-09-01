package net.nemerosa.ontrack.kdsl.acceptance.tests.search

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.general.label
import net.nemerosa.ontrack.kdsl.spec.search.search
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ACCDSLSearch : AbstractACCDSLTestSupport() {

    @Test
    @Disabled
    fun `Searching for a build based on its release information`() {
        val value = uid("V")
        val value1 = uid("V1")
        val value2 = uid("V2")
        project {
            branch("test") {
                val build1 = build("1", "Build 1") {
                    label = "$value-$value1"
                    this
                }
                val build2 = build("2", "Build 2") {
                    label = "$value-$value2"
                    this
                }

                // Checks that we find one build on exact match
                var results = ontrack.search("$value-$value1")
                val build1Result =
                    results.items.find { it.title == "Build ${build1.branch.project.name}/${build1.branch.name}/${build1.name}" }
                assertNotNull(build1Result, "Build 1 found")
                val build2Result =
                    results.items.find { it.title == "Build ${build2.branch.project.name}/${build2.branch.name}/${build2.name}" }
                assertNull(build2Result, "Build 2 not found")


                // Checks that we find two builds on prefix match
                results = ontrack.search(value)
                assertNotNull(
                    results.items.find { it.title == "Build ${build1.branch.project.name}/${build1.branch.name}/${build1.name}" },
                    "Build 1 found"
                )
                assertNotNull(
                    results.items.find { it.title == "Build ${build2.branch.project.name}/${build2.branch.name}/${build2.name}" },
                    "Build 2 found"
                )
            }
        }

    }

}