package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ValidationRunStatusResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

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
                    this@project.asAccountWithProjectRole(Roles.PROJECT_OWNER) {
                        status.decorate(validationRunStatusResourceDecorator) {
                            assertLinkPresent("_comment")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Editable comment by project manager`() {
        project project@{
            branch {
                val vs = validationStamp()
                build {
                    val run = validate(vs, ValidationRunStatusID.STATUS_FAILED, "My comment")
                    val status = run.lastStatus
                    this@project.asAccountWithProjectRole(Roles.PROJECT_MANAGER) {
                        status.decorate(validationRunStatusResourceDecorator) {
                            assertLinkPresent("_comment")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Editable comment by project validation manager`() {
        project project@{
            branch {
                val vs = validationStamp()
                build {
                    val run = validate(vs, ValidationRunStatusID.STATUS_FAILED, "My comment")
                    val status = run.lastStatus
                    this@project.asAccountWithProjectRole(Roles.PROJECT_VALIDATION_MANAGER) {
                        status.decorate(validationRunStatusResourceDecorator) {
                            assertLinkPresent("_comment")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Editable comment by global validation manager`() {
        project project@{
            branch {
                val vs = validationStamp()
                build {
                    val run = validate(vs, ValidationRunStatusID.STATUS_FAILED, "My comment")
                    val status = run.lastStatus
                    asAccountWithGlobalRole(Roles.GLOBAL_VALIDATION_MANAGER) {
                        status.decorate(validationRunStatusResourceDecorator) {
                            assertLinkPresent("_comment")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Editable comment by same user if participant`() {
        project project@{
            branch {
                val vs = validationStamp()
                build {
                    withGrantViewToAll {
                        this@project.asAccountWithProjectRole(Roles.PROJECT_PARTICIPANT) {
                            val run = validate(vs, ValidationRunStatusID.STATUS_FAILED, "My comment")
                            val status = run.lastStatus
                            status.decorate(validationRunStatusResourceDecorator) {
                                assertLinkPresent("_comment")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Not editable comment by same user if not participant`() {
        project project@{
            branch {
                val vs = validationStamp()
                build {
                    val status = this@project.asAccountWithProjectRole(Roles.PROJECT_PARTICIPANT) {
                        val run = validate(vs, ValidationRunStatusID.STATUS_FAILED, "My comment")
                        run.lastStatus
                    }
                    withGrantViewToAll {
                        asUserWithView {
                            status.decorate(validationRunStatusResourceDecorator) {
                                assertLinkNotPresent("_comment")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Not editable comment by anonymous`() {
        project project@{
            branch {
                val vs = validationStamp()
                build {
                    val status = this@project.asAccountWithProjectRole(Roles.PROJECT_PARTICIPANT) {
                        val run = validate(vs, ValidationRunStatusID.STATUS_FAILED, "My comment")
                        run.lastStatus
                    }
                    withGrantViewToAll {
                        asAnonymous {
                            status.decorate(validationRunStatusResourceDecorator) {
                                assertLinkNotPresent("_comment")
                            }
                        }
                    }
                }
            }
        }
    }

}