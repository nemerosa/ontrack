package net.nemerosa.ontrack.acceptance.boot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JUnitAcceptanceRunner implements AcceptanceRunner {

    private final Logger logger = LoggerFactory.getLogger(JUnitAcceptanceRunner)
    private final AcceptanceConfig config

    @Autowired
    JUnitAcceptanceRunner(AcceptanceConfig config) {
        this.config = config
    }

    void run() throws Exception {
        logger.info "Starting acceptance tests."
        logger.info "Config: ${config}"
    }
}
