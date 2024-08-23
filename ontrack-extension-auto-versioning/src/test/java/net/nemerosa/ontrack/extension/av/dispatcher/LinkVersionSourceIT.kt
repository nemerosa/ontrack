package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.metaInfoItem
import net.nemerosa.ontrack.extension.general.metaInfoProperty
import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinkVersionSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var versionSourceFactory: VersionSourceFactory

    @Test
    fun `Link default version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    parent.linkTo(dependency)

                                    val version = versionSourceFactory.getBuildVersion(
                                        parent,
                                        "link/${dependency.project.name}"
                                    )
                                    assertEquals(dependency.name, version)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Link explicit default version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    parent.linkTo(dependency)

                                    val version = versionSourceFactory.getBuildVersion(
                                        parent,
                                        "link/${dependency.project.name}->default"
                                    )
                                    assertEquals(dependency.name, version)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Link default version with qualifier`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    parent.linkTo(dependency, qualifier = "sample")

                                    val version = versionSourceFactory.getBuildVersion(
                                        parent,
                                        "link/${dependency.project.name}/sample"
                                    )
                                    assertEquals(dependency.name, version)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Link default version with default qualifier and missing build`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    parent.linkTo(dependency, qualifier = "sample")

                                    assertFailsWith<VersionSourceNoVersionException> {
                                        versionSourceFactory.getBuildVersion(
                                            parent,
                                            "link/${dependency.project.name}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Link label version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    releaseProperty(dependency, "1.0.0")
                                    parent.linkTo(dependency)

                                    val version = versionSourceFactory.getBuildVersion(
                                        parent,
                                        "link/${dependency.project.name}->labelOnly"
                                    )
                                    assertEquals("1.0.0", version)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Link meta version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    metaInfoProperty(dependency, metaInfoItem("myVersion", "1.0.0"))
                                    parent.linkTo(dependency)

                                    val version = versionSourceFactory.getBuildVersion(
                                        parent,
                                        "link/${dependency.project.name}->metaInfo/myVersion"
                                    )
                                    assertEquals("1.0.0", version)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Link of a qualified link and meta version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    parent.linkTo(dependency)

                                    project {
                                        branch {
                                            build {
                                                val component = this

                                                metaInfoProperty(component, metaInfoItem("myVersion", "1.0.0"))
                                                dependency.linkTo(component, "sample")

                                                val version = versionSourceFactory.getBuildVersion(
                                                    parent,
                                                    "link/${dependency.project.name}->link/${component.project.name}/sample->metaInfo/myVersion"
                                                )
                                                assertEquals("1.0.0", version)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Finding build using linked build default version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    parent.linkTo(dependency)

                                    val build = versionSourceFactory.getBuildWithVersion(
                                        sourceProject = parent.project,
                                        versionSource = "link/${dependency.project.name}",
                                        version = dependency.name,
                                    )

                                    assertEquals(parent, build)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Finding build using linked build label version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this
                        project {
                            branch {
                                build {
                                    val dependency = this
                                    releaseProperty(dependency, "1.0.0")
                                    parent.linkTo(dependency)

                                    val build = versionSourceFactory.getBuildWithVersion(
                                        sourceProject = parent.project,
                                        versionSource = "link/${dependency.project.name}->labelOnly",
                                        version = "1.0.0",
                                    )

                                    assertEquals(parent, build)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Finding build using linked builds and meta version`() {
        asAdmin {
            project {
                branch {
                    build {
                        val parent = this

                        project {
                            branch {
                                build {
                                    val dependency = this
                                    parent.linkTo(dependency, "sample")

                                    project {
                                        branch {
                                            build {
                                                val component = this
                                                metaInfoProperty(component, metaInfoItem("myVersion", "1.0.0"))
                                                dependency.linkTo(component)

                                                val build = versionSourceFactory.getBuildWithVersion(
                                                    sourceProject = parent.project,
                                                    versionSource = "link/${dependency.project.name}/sample->link/${component.project.name}->metaInfo/myVersion",
                                                    version = "1.0.0",
                                                )

                                                assertEquals(parent, build)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}