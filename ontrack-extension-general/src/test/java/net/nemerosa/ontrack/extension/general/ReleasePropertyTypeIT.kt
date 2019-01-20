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
                    release("1.1.0")
                }
                val build = build {
                    release("1.2.0")
                }

                assertBuildSearch {
                    it.withWithProperty(ReleasePropertyType::class.java.name)
                            .withWithPropertyValue("1.2")
                } returns build
            }
        }
    }

    @Test
    fun `Search since release property`() {
        project {
            branch branch@{
                build {}
                build {}
                val build1 = build {
                    release("1.1.0")
                }
                val build2 = build {}
                val build3 = build {
                    release("1.2.0")
                }

                assertBuildSearch {
                    it.withSinceProperty(ReleasePropertyType::class.java.name)
                } returns build3

                assertBuildSearch {
                    it.withSinceProperty(ReleasePropertyType::class.java.name)
                            .withSincePropertyValue("1.1.0")
                } returns listOf(build3, build2, build1)
            }
        }
    }

}