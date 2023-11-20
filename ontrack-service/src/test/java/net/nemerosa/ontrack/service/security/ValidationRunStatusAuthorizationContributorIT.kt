package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ValidationRunStatusAuthorizationContributorIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var contributor: ValidationRunStatusAuthorizationContributor

    @Test
    fun `Authorizations because admin`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    build {
                        val run = validate(vs)
                        val status = run.lastStatus
                        assertTrue(contributor.appliesTo(status), "Contributor applies to VRS")
                        val auths = contributor.getAuthorizations(securityService.currentAccount!!, status)
                        assertNotNull(auths.find { it.name == "validation_run_status" && it.action == "comment_change" }) {
                            assertEquals(true, it.authorized, "Comment change is authorized")
                        }
                    }
                }
            }
        }
    }

}