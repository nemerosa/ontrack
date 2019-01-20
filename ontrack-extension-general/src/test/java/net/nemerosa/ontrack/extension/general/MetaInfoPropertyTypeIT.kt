package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import org.junit.Test

class MetaInfoPropertyTypeIT : AbstractPropertyTypeIT() {

    @Test
    fun `Search by meta value`() {
        search { _, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("2.1")
            } returns listOf(
                    b2
            )
        }
    }

    @Test
    fun `Search by meta value pattern`() {
        search { b1, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("2.*")
            } returns listOf(
                    b2, b1
            )
        }
    }

    @Test
    fun `Search by meta name`() {
        search { _, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("kotlin:")
            } returns listOf(
                    b2
            )
        }
    }

    @Test
    fun `Search by meta name with pattern`() {
        search { b1, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("java:1.*")
            } returns listOf(
                    b2, b1
            )
        }
    }

    private fun search(test: Branch.(b1: Build, b2: Build) -> Unit) {
        project {
            branch branch@{
                build {}
                build {}
                val b1 = build {
                    setProperty<MetaInfoProperty, MetaInfoPropertyType>(
                            MetaInfoProperty(
                                    listOf(
                                            MetaInfoPropertyItem("java", "1.7", "", ""),
                                            MetaInfoPropertyItem("boot", "1.5", "", ""),
                                            MetaInfoPropertyItem("version", "2.0", "", "")
                                    )
                            )
                    )
                }
                val b2 = build {
                    setProperty<MetaInfoProperty, MetaInfoPropertyType>(
                            MetaInfoProperty(
                                    listOf(
                                            MetaInfoPropertyItem("java", "1.8", "", ""),
                                            MetaInfoPropertyItem("kotlin", "1.2", "", ""),
                                            MetaInfoPropertyItem("boot", "1.5", "", ""),
                                            MetaInfoPropertyItem("version", "2.1", "", "")
                                    )
                            )
                    )
                }

                this@branch.test(b1, b2)
            }
        }
    }

}