package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ValidationRunStatusResourceDecoratorIT: AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var validationRunStatusResourceDecorator: ValidationRunStatusResourceDecorator

    @Test
    fun `Editable comment by project owner`() {
        project project@{
            branch {
                val vs = validationStamp()
                build {
                    val run = validate(vs, ValidationRunStatusID.STATUS_FAILED, "My comment")
                    val status = run.lastStatus
                    // Project owner
                    this@project.asAccountWithProjectRole(Roles.PROJECT_OWNER) {
                        status.decorate(validationRunStatusResourceDecorator) {
                            assertLinkPresent("_comment")
                        }
                    }
                }
            }
        }
    }

}