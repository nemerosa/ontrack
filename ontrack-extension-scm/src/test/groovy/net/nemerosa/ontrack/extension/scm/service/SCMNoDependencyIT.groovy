package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.it.AbstractServiceTestJUnit4Support
import org.junit.Test

/**
 * Tests that the SCM module does not need any specific implementation to be loaded (Git, SVN).
 *
 * This allows extension modules to use the SCM module for their tests.
 */
class SCMNoDependencyIT extends AbstractServiceTestJUnit4Support {

    @Test
    void 'Loaded'() {
        // It's enough for the test class to be loaded OK
    }

}
