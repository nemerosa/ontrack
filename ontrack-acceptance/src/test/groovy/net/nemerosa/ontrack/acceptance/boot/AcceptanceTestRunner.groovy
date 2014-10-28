package net.nemerosa.ontrack.acceptance.boot

import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.InitializationError

class AcceptanceTestRunner extends BlockJUnit4ClassRunner {

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @throws InitializationError if the test class is malformed.
     */
    AcceptanceTestRunner(Class<?> klass) throws InitializationError {
        super(klass)
    }

}
