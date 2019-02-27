package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.model.security.Roles
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class BranchResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var branchResourceDecorator: BranchResourceDecorator

    @Test
    fun `_allValidationStampFilters link granted to all`() {
        project {
            branch {
                withNoGrantViewToAll {
                    asUserWithView(this) {
                        decorate(branchResourceDecorator) {
                            assertLinkPresent("_allValidationStampFilters")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `_validationStampFilterCreate link not granted to all`() {
        project {
            branch {
                asUserWithView(this) {
                    decorate(branchResourceDecorator) {
                        assertLinkNotPresent("_validationStampFilterCreate")
                    }
                }
            }
        }
    }

    @Test
    fun `_validationStampFilterCreate link granted to participants`() {
        project {
            branch {
                asAccountWithProjectRole(Roles.PROJECT_PARTICIPANT) {
                    decorate(branchResourceDecorator) {
                        assertLinkPresent("_validationStampFilterCreate")
                    }
                }
            }
        }
    }

    @Test
    fun `_validationStampFilterCreate link granted to validation stamp managers`() {
        project {
            branch {
                asAccountWithProjectRole(Roles.PROJECT_VALIDATION_MANAGER) {
                    decorate(branchResourceDecorator) {
                        assertLinkPresent("_validationStampFilterCreate")
                    }
                }
            }
        }
    }

    @Test
    fun `_validationStampFilterCreate link granted to project managers`() {
        project {
            branch {
                asAccountWithProjectRole(Roles.PROJECT_MANAGER) {
                    decorate(branchResourceDecorator) {
                        assertLinkPresent("_validationStampFilterCreate")
                    }
                }
            }
        }
    }

}