package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.model.structure.Reordering
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoPromotionLevelPropertyIT : AbstractDSLTestSupport() {

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
