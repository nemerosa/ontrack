package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.PromotionRunService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PromotionRunServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var promotionRunService: PromotionRunService

    @Test
    fun `Getting last promotion run for a promotion in a project`() {
        val plName = uid("pl_")
        project {
            branch {
                val branch1 = this
                val pl1 = promotionLevel(name = plName)
                build {
                    promote(pl1)
                }
                branch {
                    val pl2 = promotionLevel(name = plName)
                    build {
                        promote(pl2)
                    }
                    // Promotes a new build in (1)
                    branch1.apply {
                        val build1 = build {
                            promote(pl1)
                        }
                        // Looking for the last promotion run
                        assertNotNull(promotionRunService.getLastPromotionRunForProject(project, plName)) { run ->
                            assertEquals(pl1, run.promotionLevel, "Promotion on branch 1")
                            assertEquals(build1, run.build, "Build on branch 1")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Not getting any last promotion run for a promotion in a project`() {
        val plName = uid("pl_")
        project {
            branch {
                val branch1 = this
                val pl1 = promotionLevel(name = plName)
                build {
                    promote(pl1)
                }
                branch {
                    val pl2 = promotionLevel(name = plName)
                    build {
                        promote(pl2)
                    }
                    // Promotes a new build in (1)
                    branch1.apply {
                        build {
                            promote(pl1)
                        }
                        // Looking for the last promotion run
                        // using another name altogether
                        assertNull(
                            promotionRunService.getLastPromotionRunForProject(project, uid("pl_")),
                            "Not find any last run"
                        )
                    }
                }
            }
        }
    }

}