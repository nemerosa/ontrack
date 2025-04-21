package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class ReleasePropertyTypeIT : AbstractPropertyTypeTestSupport() {

    @Autowired
    private lateinit var releasePropertyType: ReleasePropertyType

    @Test
    fun `Case insensitive search on release property`() {
        project {
            branch branch@{
                val build = build {
                    release("Release one")
                }
                assertBuildSearch {
                    it.withWithProperty(ReleasePropertyType::class.java.name)
                            .withWithPropertyValue("*ONE")
                } returns build
            }
        }
    }

    @Test
    fun `Exact case insensitive search on release property`() {
        project {
            branch branch@{
                val build = build {
                    release("Release one")
                }
                assertBuildSearch {
                    it.withWithProperty(ReleasePropertyType::class.java.name)
                            .withWithPropertyValue("RELEASE ONE")
                } returns build
            }
        }
    }

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
                            .withWithPropertyValue("1.2.*")
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

    @Test
    fun getEditionForm_empty() {
        project {
            branch {
                build {
                    val form = releasePropertyType.getEditionForm(this, null)
                    assertEquals(null, form.getField("name")?.value)
                }
            }
        }
    }

    @Test
    fun getEditionForm_not_empty() {
        project {
            branch {
                build {
                    val form = releasePropertyType.getEditionForm(this, ReleaseProperty("test"))
                    assertEquals("test", form.getField("name")?.value)
                }
            }
        }
    }

    @Test
    fun `Build branch search on release property`() {
        project {
            branch {
                val builds = (0..5).map {
                    build(it.toString()) {
                        setProperty(
                            this,
                            ReleasePropertyType::class.java,
                            ReleaseProperty(name = "1.0.$it")
                        )
                    }
                }
                (0..5).forEach {
                    val build = propertyService.findBuildByBranchAndSearchkey(id, ReleasePropertyType::class.java, "1.0.$it")
                    assertEquals(builds[it].id, build, "Build found using its release property")
                }
            }
        }
    }

}