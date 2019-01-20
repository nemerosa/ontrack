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
                    metaInfo(
                            "java" to "1.7",
                            "boot" to "1.5",
                            "version" to "2.0"
                    )
                }
                val b2 = build {
                    metaInfo(
                            "java" to "1.8",
                            "kotlin" to "1.2",
                            "boot" to "1.5",
                            "version" to "2.1"
                    )
                }

                this@branch.test(b1, b2)
            }
        }
    }

    @Test
    fun `Since meta info`() {
        project {
            branch branch@{
                build {}
                build {}
                val build1 = build {
                    metaInfo(
                            "java" to "1.7"
                    )
                }
                val build2 = build {}
                val build3 = build {
                    metaInfo(
                            "java" to "1.8"
                    )
                }

                assertBuildSearch {
                    it.withSinceProperty(MetaInfoPropertyType::class.java.name)
                } returns build3

                assertBuildSearch {
                    it.withSinceProperty(MetaInfoPropertyType::class.java.name)
                            .withSincePropertyValue("1.7")
                } returns listOf(build3, build2, build1)
            }
        }
    }

}