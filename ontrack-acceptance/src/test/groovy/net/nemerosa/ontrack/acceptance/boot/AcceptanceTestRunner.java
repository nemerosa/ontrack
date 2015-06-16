package net.nemerosa.ontrack.acceptance.boot;

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class AcceptanceTestRunner extends BlockJUnit4ClassRunner {

    private AcceptanceConfig config;

    public AcceptanceTestRunner(Class<?> klass, AcceptanceConfig config) throws InitializationError {
        super(klass);
        this.config = config;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        AcceptanceTest acceptanceTest = method.getAnnotation(AcceptanceTest.class);
        if (acceptanceTest != null && !config.acceptTest(acceptanceTest)) {
            notifier.fireTestIgnored(describeChild(method));
            return;
        }
        // Default
        super.runChild(method, notifier);
    }
}
