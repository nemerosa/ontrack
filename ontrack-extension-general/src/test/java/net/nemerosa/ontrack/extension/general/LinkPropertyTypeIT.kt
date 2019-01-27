package net.nemerosa.ontrack.extension.general

import org.junit.Test

class LinkPropertyTypeIT : AbstractPropertyTypeIT() {

    @Test
    fun `Search by link`() {
        project {
            branch branch@{
                build {}
                build {}
                val build1 = build {
                    links("link" to "https://en.wikipedia.org")
                }
                val build2 = build {}
                val build3 = build {
                    links("link" to "https://fr.wikipedia.org")
                }

                assertBuildSearch {
                    it.withWithProperty(LinkPropertyType::class.java.name)
                } returns listOf(build3, build1)

                assertBuildSearch {
                    it.withWithProperty(LinkPropertyType::class.java.name)
                            .withWithPropertyValue("fr")
                } returns build3

                assertBuildSearch {
                    it.withWithProperty(LinkPropertyType::class.java.name)
                            .withWithPropertyValue("en")
                } returns build1

                assertBuildSearch {
                    it.withSinceProperty(LinkPropertyType::class.java.name)
                } returns build3

                assertBuildSearch {
                    it.withSinceProperty(LinkPropertyType::class.java.name)
                            .withSincePropertyValue("en")
                } returns listOf(build3, build2, build1)
            }
        }
    }

}