package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals

class MessagePropertyTypeIT : AbstractPropertyTypeIT() {

    @Test
    fun `Search based on message property`() {
        val prefix = uid("M")
        project {
            branch branch@{
                build {}
                build {}
                val build = build {
                    setProperty(
                            this,
                            MessagePropertyType::class.java,
                            MessageProperty(
                                    MessageType.INFO,
                                    "$prefix starts my message"
                            )
                    )
                }
                // Performs a search for this build
                val filterProviderData = buildFilterService.standardFilterProviderData(1)
                        .withWithProperty(MessagePropertyType::class.java.name)
                        .withWithPropertyValue(prefix)
                        .build()
                val builds = filterProviderData.filterBranchBuilds(this@branch)
                assertEquals(1, builds.size)
                val result = builds[0]
                assertEquals(build.id, result.id)
            }
        }
    }

}