package net.nemerosa.ontrack.extension.general

import org.junit.Test

class ReleasePropertyTypeIT : AbstractPropertyTypeIT() {

    @Test
    fun `Search on release property`() {
        project {
            branch branch@{
                build {}
                build {}
                build {
                    setProperty<ReleaseProperty, ReleasePropertyType>(ReleaseProperty("1.1.0"))
                }
                val build = build {
                    setProperty<ReleaseProperty, ReleasePropertyType>(ReleaseProperty("1.2.0"))
                }

                assertBuildSearch {
                    it.withWithProperty(ReleasePropertyType::class.java.name)
                            .withWithPropertyValue("1.2")
                } returns build
            }
        }
    }

}