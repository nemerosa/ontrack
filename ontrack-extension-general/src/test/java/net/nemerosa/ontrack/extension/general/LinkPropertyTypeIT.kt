package net.nemerosa.ontrack.extension.general

import org.junit.Test

class LinkPropertyTypeIT : AbstractPropertyTypeIT() {

    @Test
    fun `Search by link`() {
        project {
            branch branch@{
                build {}
                build {}
                build {
                    setProperty<LinkProperty, LinkPropertyType>(
                            LinkProperty.of(
                                    "link",
                                    "https://en.wikipedia.org"
                            )
                    )
                }
                val build2 = build {
                    setProperty<LinkProperty, LinkPropertyType>(
                            LinkProperty.of(
                                    "link",
                                    "https://fr.wikipedia.org"
                            )
                    )
                }
                assertBuildSearch {
                    it.withWithProperty(LinkPropertyType::class.java.name)
                            .withWithPropertyValue("fr")
                } returns build2
            }
        }
    }

}