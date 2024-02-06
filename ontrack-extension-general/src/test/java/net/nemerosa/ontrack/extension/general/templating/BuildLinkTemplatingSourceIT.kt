package net.nemerosa.ontrack.extension.general.templating

import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BuildLinkTemplatingSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var templatingService: TemplatingService

    @Test
    fun `Not existing link`() {
        asAdmin {
            project {
                val target = branch<Build> {
                    build("1.2.3")
                }

                project {
                    branch {
                        build {
                            // No link linkTo(target)

                            val text = templatingService.render(
                                template = "${'$'}{build.linked?project=${target.project.name}}",
                                context = mapOf("build" to this),
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            assertEquals("", text, "No link, no text.")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Linked name`() {
        asAdmin {
            project {
                val target = branch<Build> {
                    build("1.2.3")
                }

                project {
                    branch {
                        build {
                            linkTo(target)

                            val text = templatingService.render(
                                template = "${'$'}{build.linked?project=${target.project.name}}",
                                context = mapOf("build" to this),
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            assertEquals("1.2.3", text)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Linked name with qualifier`() {
        asAdmin {
            project {
                val target = branch<Build> {
                    build("1.2.3")
                }

                project {
                    branch {
                        build {
                            linkTo(target, qualifier = "special")

                            val text = templatingService.render(
                                template = "${'$'}{build.linked?project=${target.project.name}&qualifier=special}",
                                context = mapOf("build" to this),
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            assertEquals("1.2.3", text)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Linked name by default`() {
        asAdmin {
            project {
                val target = branch<Build> {
                    build("1.2.3") {
                        releaseProperty(this, "v1.2.3")
                    }
                }

                project {
                    branch {
                        build {
                            linkTo(target)

                            val text = templatingService.render(
                                template = "${'$'}{build.linked?project=${target.project.name}}",
                                context = mapOf("build" to this),
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            assertEquals("1.2.3", text)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Linked release`() {
        asAdmin {
            project {
                val target = branch<Build> {
                    build("1.2.3") {
                        releaseProperty(this, "v1.2.3")
                    }
                }

                project {
                    branch {
                        build {
                            linkTo(target)

                            val text = templatingService.render(
                                template = "${'$'}{build.linked?project=${target.project.name}&mode=release}",
                                context = mapOf("build" to this),
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            assertEquals("v1.2.3", text)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Linked name if no release in auto mode`() {
        asAdmin {
            project {
                val target = branch<Build> {
                    build("1.2.3")
                }

                project {
                    branch {
                        build {
                            linkTo(target)

                            val text = templatingService.render(
                                template = "${'$'}{build.linked?project=${target.project.name}&mode=auto}",
                                context = mapOf("build" to this),
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            assertEquals("1.2.3", text)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Failure if no release property when mode is release`() {
        asAdmin {
            project {
                val target = branch<Build> {
                    build("1.2.3")
                }

                project {
                    branch {
                        build {
                            linkTo(target)

                            val text = templatingService.render(
                                template = "${'$'}{build.linked?project=${target.project.name}&mode=release}",
                                context = mapOf("build" to this),
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            assertEquals("#error", text)
                        }
                    }
                }
            }
        }
    }

}