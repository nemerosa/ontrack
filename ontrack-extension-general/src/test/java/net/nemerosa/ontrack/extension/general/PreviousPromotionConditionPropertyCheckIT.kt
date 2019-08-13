package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.junit.Test
import kotlin.test.assertFailsWith

class PreviousPromotionConditionPropertyCheckIT : AbstractGeneralExtensionTestSupport() {

    @Test
    fun `No promotion check`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion no check at settings level`() {
        withPreviousPromotionGlobalCondition(false) {
            project {
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at settings level with build promoted`() {
        withPreviousPromotionGlobalCondition(true) {
            project {
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at settings level with build not promoted`() {
        withPreviousPromotionGlobalCondition(true) {
            project {
                branch {
                    promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        assertFailsWith<PreviousPromotionRequiredGlobalException> {
                            promote(pl2)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion no check at promotion level`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel {
                        previousPromotionCondition(false)
                    }
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at promotion level with build promoted`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel {
                        previousPromotionCondition(true)
                    }
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at promotion level with build not promoted`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    promotionLevel()
                    val pl2 = promotionLevel {
                        previousPromotionCondition(true)
                    }
                    build {
                        assertFailsWith<PreviousPromotionRequiredException> {
                            promote(pl2)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion no check at branch level`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    previousPromotionCondition(false)
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at branch level with build promoted`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    previousPromotionCondition(true)
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at branch level with build not promoted`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    previousPromotionCondition(true)
                    promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        assertFailsWith<PreviousPromotionRequiredException> {
                            promote(pl2)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion no check at project level`() {
        withPreviousPromotionGlobalCondition {
            project {
                previousPromotionCondition(false)
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at project level with build promoted`() {
        withPreviousPromotionGlobalCondition {
            project {
                previousPromotionCondition(true)
                branch {
                    val pl1 = promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl1)
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check at project level with build not promoted`() {
        withPreviousPromotionGlobalCondition {
            project {
                previousPromotionCondition(true)
                branch {
                    promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        assertFailsWith<PreviousPromotionRequiredException> {
                            promote(pl2)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check takes precedence from branch to project`() {
        withPreviousPromotionGlobalCondition {
            project {
                previousPromotionCondition(true)
                branch {
                    previousPromotionCondition(false)
                    promotionLevel()
                    val pl2 = promotionLevel()
                    build {
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check takes precedence from promotion level to project`() {
        withPreviousPromotionGlobalCondition {
            project {
                previousPromotionCondition(true)
                branch {
                    promotionLevel()
                    val pl2 = promotionLevel {
                        previousPromotionCondition(false)
                    }
                    build {
                        promote(pl2)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion check takes precedence from promotion level to branch`() {
        withPreviousPromotionGlobalCondition {
            project {
                branch {
                    previousPromotionCondition(true)
                    promotionLevel()
                    val pl2 = promotionLevel {
                        previousPromotionCondition(false)
                    }
                    build {
                        promote(pl2)
                    }
                }
            }
        }
    }

    private fun <T : ProjectEntity> T.previousPromotionCondition(flag: Boolean) {
        setProperty(
                this,
                PreviousPromotionConditionPropertyType::class.java,
                PreviousPromotionConditionProperty(previousPromotionRequired = flag)
        )
    }

    private fun withPreviousPromotionGlobalCondition(settings: Boolean = false, code: () -> Unit) {
        val previous = settingsService.getCachedSettings(PreviousPromotionConditionSettings::class.java)
        try {
            asUser().with(GlobalSettings::class.java).execute {
                settingsManagerService.saveSettings(
                        PreviousPromotionConditionSettings(
                                previousPromotionRequired = settings
                        )
                )
            }
            code()
        } finally {
            asUser().with(GlobalSettings::class.java).execute {
                settingsManagerService.saveSettings(previous)
            }
        }
    }

}
