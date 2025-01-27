package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.PromotionLevelService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PromotionLevelServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var promotionLevelService: PromotionLevelService

    @Test
    fun `Promotion level names for a project using a token`() {
        doTest(
            token = "ACC",
            expected = listOf(
                "ACCEPTANCE",
                "KDSL-ACCEPTANCE",
            )
        )
    }

    @Test
    fun `Promotion level names for a project using an empty token`() {
        doTest(
            token = "",
            expected = listOf(
                "ACCEPTANCE",
                "KDSL-ACCEPTANCE",
                "PERFORMANCES"
            )
        )
    }

    @Test
    fun `Promotion level names for a project using a null token`() {
        doTest(
            token = null,
            expected = listOf(
                "ACCEPTANCE",
                "KDSL-ACCEPTANCE",
                "PERFORMANCES"
            )
        )
    }

    private fun doTest(
        token: String?,
        expected: List<String>,
    ) {
        project {
            branch("main") {
                promotionLevel("ACCEPTANCE")
            }
            branch("staging") {
                promotionLevel("KDSL-ACCEPTANCE")
                promotionLevel("PERFORMANCES")
            }
            val names = promotionLevelService.findPromotionLevelNamesByProject(
                project = this,
                token = token,
            )
            assertEquals(expected, names)
        }
    }

}