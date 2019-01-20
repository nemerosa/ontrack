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
                    setProperty(
                            this,
                            MessagePropertyType::class.java,
                            MessageProperty(
                                    MessageType.INFO,
                                    "$prefix starts my message"
                            )
                    )
                }
                assertBuildSearch {
                    it.withWithProperty(MessagePropertyType::class.java.name)
                            .withWithPropertyValue(prefix)
                } returns build
            }
        }
    }

}