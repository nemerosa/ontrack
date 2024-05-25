package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.ValidationRunService
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationRunServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var validationRunService: ValidationRunService

    @Test
    fun `Checking for a passed build when no validation run`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    build {
                        // No validation at all
                        assertFalse(validationRunService.isValidationRunPassed(this, vs))
                    }
                }
            }
        }
    }

    @Test
    fun `Checking for a passed build when only one passed validation`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    build {
                        validate(vs, validationRunStatusID = ValidationRunStatusID.STATUS_PASSED)
                        assertTrue(validationRunService.isValidationRunPassed(this, vs))
                    }
                }
            }
        }
    }

    @Test
    fun `Checking for a passed build when only one failed validation`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    build {
                        validate(vs, validationRunStatusID = ValidationRunStatusID.STATUS_FAILED)
                        assertFalse(validationRunService.isValidationRunPassed(this, vs))
                    }
                }
            }
        }
    }

    @Test
    fun `Checking for a passed build when last validation is passed`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    build {
                        validate(vs, validationRunStatusID = ValidationRunStatusID.STATUS_FAILED)
                        validate(vs, validationRunStatusID = ValidationRunStatusID.STATUS_PASSED)
                        assertTrue(validationRunService.isValidationRunPassed(this, vs))
                    }
                }
            }
        }
    }

    @Test
    fun `Checking for a passed build when last validation is failed`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    build {
                        validate(vs, validationRunStatusID = ValidationRunStatusID.STATUS_PASSED)
                        validate(vs, validationRunStatusID = ValidationRunStatusID.STATUS_FAILED)
                        assertFalse(validationRunService.isValidationRunPassed(this, vs))
                    }
                }
            }
        }
    }

}