package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test

class MessagePropertyTypeIT : AbstractPropertyTypeIT() {

    @Test
    fun `Search based on message property`() {
        val prefix = uid("M")
        project {
            branch branch@{
                build {}
                build {}
                val build = build {
                    message("$prefix starts my message")
                }
                assertBuildSearch {
                    it.withWithProperty(MessagePropertyType::class.java.name)
                            .withWithPropertyValue(prefix)
                } returns build
            }
        }
    }

    @Test
    fun `Search based since message property`() {
        project {
            branch branch@{
                build {}
                build {}
                val build1 = build {
                    message("Message one")
                }
                val build2 = build()
                val build3 = build {
                    message("Message two")
                }

                assertBuildSearch {
                    it.withSinceProperty(MessagePropertyType::class.java.name)
                } returns listOf(build3)

                assertBuildSearch {
                    it.withSinceProperty(MessagePropertyType::class.java.name)
                            .withSincePropertyValue("one")
                } returns listOf(build3, build2, build1)
            }
        }
    }

}