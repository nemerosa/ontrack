package net.nemerosa.ontrack.extension.general.templating

import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.MarkdownEventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LastPromotionTemplatingFunctionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var templatingService: TemplatingService

    @Autowired
    private lateinit var markdownEventRenderer: MarkdownEventRenderer

    @Test
    fun `Last promotion on a project`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    val last = build {
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "[${last.name}](http://localhost:8080/#/build/${last.id})",
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Last promotion on a branch`() {
        asAdmin {
            project {
                val last = branch<Build>("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    build {
                        promote(pl)
                    }
                }
                val lastMain = branch<Build>("main") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                }

                val text = templatingService.render(
                    "\${#.lastPromotion?project=${project.name}&branch=previous&promotion=BRONZE}",
                    context = emptyMap(),
                    renderer = markdownEventRenderer,
                )

                assertEquals(
                    "[${last.name}](http://localhost:8080/#/build/${last.id})",
                    text
                )

                // On the project as well

                assertEquals(
                    "[${lastMain.name}](http://localhost:8080/#/build/${lastMain.id})",
                    templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )
                )
            }
        }
    }

    @Test
    fun `Last promotion on a project with explicit auto`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    val last = build {
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE&name=auto}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "[${last.name}](http://localhost:8080/#/build/${last.id})",
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Last promotion on a project with explicit auto and release available`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    val last = build {
                        releaseProperty(this, "1.0.0")
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE&name=auto}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "[1.0.0](http://localhost:8080/#/build/${last.id})",
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Last promotion on a project with release and no link`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    build {
                        releaseProperty(this, "1.0.0")
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE&link=false}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "1.0.0",
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Last promotion on a project with release and no link and filter`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    build {
                        releaseProperty(this, "1.0.0")
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE&link=false|strong}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "**1.0.0**",
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Last promotion on a project with explicit release and none available`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    build {
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE&name=release}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "#error",
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Last promotion on a project with explicit release`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    val last = build {
                        releaseProperty(this, "1.0.0")
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE&name=release}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "[1.0.0](http://localhost:8080/#/build/${last.id})",
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Last promotion on a project with name only`() {
        asAdmin {
            project {
                branch("previous") {
                    val pl = promotionLevel("BRONZE")
                    build {}
                    build {
                        promote(pl)
                    }
                    val last = build {
                        releaseProperty(this, "1.0.0")
                        promote(pl)
                    }

                    val text = templatingService.render(
                        "\${#.lastPromotion?project=${project.name}&promotion=BRONZE&name=name}",
                        context = emptyMap(),
                        renderer = markdownEventRenderer,
                    )

                    assertEquals(
                        "[${last.name}](http://localhost:8080/#/build/${last.id})",
                        text
                    )
                }
            }
        }
    }

}