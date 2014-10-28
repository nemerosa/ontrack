package net.nemerosa.ontrack.acceptance.boot

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError

class AcceptanceTestRunner extends BlockJUnit4ClassRunner {

    private AcceptanceConfig config

    AcceptanceTestRunner(Class<?> klass, AcceptanceConfig config) throws InitializationError {
        super(klass)
        this.config = config
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        def acceptanceTest = method.getAnnotation(AcceptanceTest)
        if (acceptanceTest) {
            def context = new HashSet<>(config.context)
            def excludes = acceptanceTest.excludes()
            if (!context.disjoint(excludes as Set)) {
                notifier.fireTestIgnored(describeChild(method))
                return
            }
        }
        // Default
        super.runChild(method, notifier)
    }
}
