package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.Reordering
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AutoPromotionLevelPropertyIT : AbstractDSLTestSupport() {

    @Test
    fun `Creating a promotion run with an existing promotion level`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        val run = structureService.newPromotionRun(
                            PromotionRun.of(
                                build = this,
                                promotionLevel = pl,
                                signature = Signature.of("test"),
                                description = null,
                            )
                        )
                        assertTrue(run.id.isSet, "Run has been created")
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run with an existing predefined promotion level without auto creation fails`() {
        asAdmin {
            project {
                branch {
                    val ppl = predefinedPromotionLevel()
                    build {
                        assertFailsWith<PromotionLevelNotFoundException> {
                            structureService.getOrCreatePromotionLevel(
                                branch = branch,
                                promotionLevelId = null,
                                promotionLevelName = ppl.name,
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run with an existing predefined promotion level with auto creation`() {
        asAdmin {
            project {
                autoPromotionLevelProperty(this, autoCreate = true)
                branch {
                    val ppl = predefinedPromotionLevel()
                    build {
                        val pl = structureService.getOrCreatePromotionLevel(
                            branch = branch,
                            promotionLevelId = null,
                            promotionLevelName = ppl.name,
                        )
                        val run = structureService.newPromotionRun(
                            PromotionRun.of(
                                build = this,
                                promotionLevel = pl,
                                signature = Signature.of("test"),
                                description = null,
                            )
                        )
                        assertTrue(run.id.isSet, "Run has been created")
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run without an existing predefined promotion level with auto creation fails`() {
        asAdmin {
            project {
                autoPromotionLevelProperty(this, autoCreate = true)
                branch {
                    build {
                        val plName = uid("pl_")
                        assertFailsWith<PromotionLevelNotFoundException> {
                            structureService.getOrCreatePromotionLevel(
                                branch = branch,
                                promotionLevelId = null,
                                promotionLevelName = plName,
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Auto creation of promotion levels must preserve the order`() {
        asUser().with(GlobalSettings::class.java).call {
            // Clears all existing predefined promotion levels for isolation
            predefinedPromotionLevelService.predefinedPromotionLevels.forEach {
                predefinedPromotionLevelService.deletePredefinedPromotionLevel(it.id)
            }
            // Creating four predefined promotion levels
            val copper = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(nd("COPPER", ""))
            )
            val bronze = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(nd("BRONZE", ""))
            )
            val silver = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(nd("SILVER", ""))
            )
            val gold = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(nd("GOLD", ""))
            )
            // Checking their order
            assertEquals(
                listOf(
                    "COPPER", "BRONZE", "SILVER", "GOLD"
                ),
                predefinedPromotionLevelService.predefinedPromotionLevels.map { it.name }
            )
            // Reordering
            predefinedPromotionLevelService.reorderPromotionLevels(
                Reordering(
                    listOf(
                        gold.id.get(),
                        silver.id.get(),
                        bronze.id.get(),
                        copper.id.get(),
                    )
                )
            )
            // Checking their order
            assertEquals(
                listOf(
                    "GOLD", "SILVER", "BRONZE", "COPPER"
                ),
                predefinedPromotionLevelService.predefinedPromotionLevels.map { it.name }
            )
        }
        // Creating a build
        project {
            branch {
                build {
                    // Configuring the project for auto creation of promotion levels
                    asUser().withProjectFunction(project, ProjectEdit::class.java).call {
                        propertyService.editProperty(
                            project,
                            AutoPromotionLevelPropertyType::class.java,
                            AutoPromotionLevelProperty(isAutoCreate = true)
                        )
                    }
                    // Promoting the build
                    asUser().withProjectFunction(project, ProjectEdit::class.java).call {
                        structureService.getOrCreatePromotionLevel(branch, null, "BRONZE")
                        structureService.getOrCreatePromotionLevel(branch, null, "GOLD")
                        structureService.getOrCreatePromotionLevel(branch, null, "COPPER")
                        structureService.getOrCreatePromotionLevel(branch, null, "SILVER")
                    }
                    // Controlling the promotion levels which have been created for the branch
                    asAdmin {
                        assertEquals(
                            listOf(
                                "GOLD", "SILVER", "BRONZE", "COPPER"
                            ),
                            predefinedPromotionLevelService.predefinedPromotionLevels.map { it.name }
                        )
                    }
                }
            }
        }
    }
}
