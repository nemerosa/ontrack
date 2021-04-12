package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.beans.factory.annotation.Autowired

/**
 * Support for testing CasC
 */
abstract class AbstractCascTestSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascService: net.nemerosa.ontrack.extension.casc.CascService

    /**
     * Runs a CasC from a series of YAML texts
     */
    protected fun casc(vararg yaml: String) {
        cascService.runYaml(*yaml)
    }


}