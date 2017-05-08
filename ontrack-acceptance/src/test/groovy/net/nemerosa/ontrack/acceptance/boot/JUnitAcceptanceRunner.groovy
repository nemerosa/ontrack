package net.nemerosa.ontrack.acceptance.boot

import net.nemerosa.ontrack.acceptance.config.AcceptanceConfig
import net.nemerosa.ontrack.acceptance.config.AcceptanceConfigRule
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
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
        logger.info "Starting acceptance tests..."
        logger.info "Using Acceptance config..."
        config.log { logger.info it }

        // Global config
        AcceptanceConfigRule.setGlobalConfig(config)

        // JUnit runtime
        JUnitCore junit = new JUnitCore()

        // XML reporting
        XMLRunListener xmlRunListener = new XMLRunListener(System.out)
        junit.addListener(xmlRunListener)

        // Gets all the acceptance suites
        def suites = applicationContext.getBeansWithAnnotation(AcceptanceTestSuite)

        // Filters on classes
        suites = suites.findAll { name, bean ->
            def acceptanceTest = applicationContext.findAnnotationOnBean(name, AcceptanceTest)
            return config.acceptTest(acceptanceTest)
        }

        // Class names
        def classes = suites.values().collect { it.class }

        // Creates the runners
        def runners = classes.collect { new AcceptanceTestRunner(it, config) }

        // Running the tests
        boolean ok = runners
                .collect { junit.run(it) }
                .every { it.wasSuccessful() }

        // XML output
        xmlRunListener.render(new File(config.outputDir, config.resultFileName))

        // Result
        ok

    }
}
