package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.PromotionRunRequest
import net.nemerosa.ontrack.model.structure.PropertyCreationRequest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PromotionRunControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var controller: PromotionRunController

    @Test
    fun `New promotion run`() {
        project {
            branch {
                val pl = promotionLevel()
                build {
                    // Promotion run request
                    val request = PromotionRunRequest(pl.id(), "", Time.now(), "Run", emptyList())
                    // Call
                    val run = asUser().with(pl, ProjectEdit::class.java).call {
                        controller.newPromotionRun(id, request)
                    }
                    // Checks
                    assertNotNull(run)
                }
            }
        }
    }

    @Test
    fun `New promotion run with properties`() {
        project {
            branch {
                val pl = promotionLevel()
                build {
                    // Promotion run request
                    val request = PromotionRunRequest(
                            pl.id(),
                            "",
                            Time.now(),
                            "Run",
                            listOf(
                                    PropertyCreationRequest(
                                            TestSimplePropertyType::class.java.name,
                                            mapOf("value" to "Message").asJson()
                                    )
                            )
                    )
                    // Call
                    val run = asUser().with(pl, ProjectEdit::class.java).call {
                        controller.newPromotionRun(id, request)
                    }
                    // Checks
                    assertNotNull(run) {
                        val property: TestSimpleProperty? = getProperty(run, TestSimplePropertyType::class.java)
                        assertEquals("Message", property?.value)
                    }
                }
            }
        }
    }

}