package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import org.junit.jupiter.api.Test

/**
 * Tests that the SCM module does not need any specific implementation to be loaded (Git, SVN).
 *
 * This allows extension modules to use the SCM module for their tests.
 */
class SCMNoDependencyIT : AbstractServiceTestSupport() {

    @Test
    fun load() {
        // It's enough for the test class to be loaded OK
    }

}
