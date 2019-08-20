package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.junit.Test
import kotlin.test.assertFailsWith

class PromotionRunDependenciesCheckExtensionIT : AbstractGeneralExtensionTestSupport() {

    @Test
    fun `No promotion dependency check`() {
        project {
            branch {
                val iron = promotionLevel()
                val silver = promotionLevel()
                val gold = promotionLevel()
                val platinum = promotionLevel()
                build {
                    promote(iron)
                    promote(silver)
                    promote(gold)
                    promote(platinum)
                }
            }
        }
    }

    @Test
    fun `Promotion dependency check with one missing`() {
        project {
            branch {
                val iron = promotionLevel()
                @Suppress("UNUSED_VARIABLE") val silver = promotionLevel()
                val gold = promotionLevel()
                val platinum = promotionLevel {
                    promotionDependenciesCondition(gold, silver)
                }
                build {
                    promote(iron)
                    // promote(silver)
                    promote(gold)
                    assertFailsWith<PromotionDependenciesException> {
                        promote(platinum)
                    }
                }
            }
        }
    }

    @Test
    fun `Promotion dependency check with none missing`() {
        project {
            branch {
                val iron = promotionLevel()
                val silver = promotionLevel()
                val gold = promotionLevel()
                val platinum = promotionLevel {
                    promotionDependenciesCondition(gold, silver)
                }
                build {
                    promote(iron)
                    promote(silver)
                    promote(gold)
                    promote(platinum)
                }
            }
        }
    }

    @Test
    fun `Promotion dependency check with other missing`() {
        project {
            branch {
                @Suppress("UNUSED_VARIABLE") val iron = promotionLevel()
                val silver = promotionLevel()
                val gold = promotionLevel()
                val platinum = promotionLevel {
                    promotionDependenciesCondition(gold, silver)
                }
                build {
                    // promote(iron)
                    promote(silver)
                    promote(gold)
                    promote(platinum)
                }
            }
        }
    }

    private fun PromotionLevel.promotionDependenciesCondition(vararg dependencies: PromotionLevel) {
        setProperty(
                this,
                PromotionDependenciesPropertyType::class.java,
                PromotionDependenciesProperty(dependencies.map { it.name }.toList())
        )
    }

}