package net.nemerosa.ontrack.acceptance.boot

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.runner.JUnitCore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class JUnitAcceptanceRunner implements AcceptanceRunner {

    private final Logger logger = LoggerFactory.getLogger(JUnitAcceptanceRunner)
    private final ApplicationContext applicationContext
    private final AcceptanceConfig config

    @Autowired
    JUnitAcceptanceRunner(AcceptanceConfig config, ApplicationContext applicationContext) {
        this.config = config
        this.applicationContext = applicationContext
    }

    @Override
    boolean run() throws Exception {
        logger.info "Starting acceptance tests."
        logger.info "Config: ${config}"

        // Config as system properties
        config.setSystemProperties()

        // JUnit runtime
        JUnitCore junit = new JUnitCore()

        // XML reporting
        XMLRunListener xmlRunListener = new XMLRunListener(System.out)
        junit.addListener(xmlRunListener)

        // Gets all the acceptance suites
        def suites = applicationContext.getBeansWithAnnotation(AcceptanceTestSuite).values().collect { it.class }

        // Creates the runners
        def runners = suites.collect { new AcceptanceTestRunner(it) }

        // Running the tests
        boolean ok = runners
                .collect { junit.run(it) }
                .every { it.wasSuccessful() }

        // XML output
        xmlRunListener.render(new File('ontrack-acceptance.xml'))

        // Result
        ok

    }
}
