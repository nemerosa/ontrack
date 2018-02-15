package net.nemerosa.ontrack.acceptance.config

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import org.junit.Test

class AcceptanceConfigTest {

    @Test
    void 'Test accepted by default when no context'() {
        def config = new AcceptanceConfig()
        assert (config.acceptTest(AnyContext.class.getAnnotation(AcceptanceTest)))
    }

    @Test
    void 'Explicit test not accepted when no context'() {
        def config = new AcceptanceConfig()
        assert !(config.acceptTest(ExplicitContext.class.getAnnotation(AcceptanceTest)))
    }

    @Test
    void 'Explicit test accepted when context set explicitly'() {
        def config = new AcceptanceConfig()
        config.context = "explicitContext"
        assert (config.acceptTest(ExplicitContext.class.getAnnotation(AcceptanceTest)))
    }

    @Test
    void 'Other test not accepted when context set explicitly'() {
        def config = new AcceptanceConfig()
        config.context = "explicitContext"
        assert !(config.acceptTest(AnyContext.class.getAnnotation(AcceptanceTest)))
    }


    @AcceptanceTest("anyContext")
    static class AnyContext {}

    @AcceptanceTest(value = ["explicitContext"], explicit = true)
    static class ExplicitContext {}

}
