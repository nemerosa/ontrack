package net.nemerosa.ontrack.acceptance.boot;

import net.nemerosa.ontrack.acceptance.config.AcceptanceConfig;
import net.nemerosa.ontrack.acceptance.config.AcceptanceConfigRule;
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest;
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JUnitAcceptanceRunner implements AcceptanceRunner {

    private final Logger logger = LoggerFactory.getLogger(JUnitAcceptanceRunner.class);
    private final ApplicationContext applicationContext;
    private final AcceptanceConfig config;

    @Autowired
    public JUnitAcceptanceRunner(AcceptanceConfig config, ApplicationContext applicationContext) {
        this.config = config;
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean run() {
        logger.info("Starting acceptance tests...");
        logger.info("Using Acceptance config...");
        config.log(logger::info);

        // Global config
        AcceptanceConfigRule.setGlobalConfig(config);

        // JUnit runtime
        JUnitCore junit = new JUnitCore();

        // XML reporting
        XMLRunListener xmlRunListener = new XMLRunListener(System.out);
        junit.addListener(xmlRunListener);

        // Gets all the acceptance suites
        Map<String, Object> suites = applicationContext.getBeansWithAnnotation(AcceptanceTestSuite.class);

        // Filters on classes
        List<Class<?>> classes = suites.entrySet().stream().filter(entry -> {
            String name = entry.getKey();
            AcceptanceTest acceptanceTest = applicationContext.findAnnotationOnBean(name, AcceptanceTest.class);
            return config.acceptTest(acceptanceTest);
        }).map(entry -> entry.getValue().getClass()).collect(Collectors.toList());

        // Creates the runners
        List<AcceptanceTestRunner> runners = classes.stream()
                .map(this::createAcceptanceTestRunner)
                .collect(Collectors.toList());

        // Running the tests
        boolean ok = runners.stream()
                .map(junit::run)
                .allMatch(Result::wasSuccessful);

        // XML output
        xmlRunListener.render(new File(config.getOutputDir(), config.getResultFileName()));

        // Result
        return ok;

    }

    @NotNull
    private AcceptanceTestRunner createAcceptanceTestRunner(Class<?> it) {
        try {
            return new AcceptanceTestRunner(it, config);
        } catch (InitializationError initializationError) {
            throw new RuntimeException("Cannot create acceptance runner for " + it);
        }
    }
}
