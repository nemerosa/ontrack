package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import org.springframework.beans.factory.annotation.Autowired

/**
 * Support for testing CasC
 */
abstract class AbstractCascTestSupport : AbstractDSLTestJUnit4Support() {

    @Autowired
    private lateinit var cascService: net.nemerosa.ontrack.extension.casc.CascService

    /**
     * Runs a CasC from a series of YAML texts
     */
    protected fun casc(vararg yaml: String) {
        asAdmin {
            cascService.runYaml(*yaml)
        }
    }


}