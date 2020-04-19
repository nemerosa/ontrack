package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the stale property
 */
@AcceptanceTestSuite
class ACCDSLStaleProperty extends AbstractACCDSL {

    @Test
    void 'Stale property'() {

        // Creating a project and a branch
        def project = ontrack.project(uid('P'))

        // Gets the stale property
        def property = project.config.stale
        assert property.disablingDuration == 0
        assert property.deletingDuration == 0
        assert property.promotionsToKeep == []

        // Sets the stale property
        project.config {
            stale 15, 30
        }

        // Gets the stale property
        property = project.config.stale
        assert property.disablingDuration == 15
        assert property.deletingDuration == 30
        assert property.promotionsToKeep == []

    }

    @Test
    void 'Stale property with promotions to keep'() {

        // Creating a project and a branch
        def project = ontrack.project(uid('P'))

        // Sets the stale property
        project.config {
            stale 15, 30, [ 'DELIVERY', 'PRODUCTION']
        }

        // Gets the stale property
        def property = project.config.stale
        assert property.disablingDuration == 15
        assert property.deletingDuration == 30
        assert property.promotionsToKeep == [ 'DELIVERY', 'PRODUCTION']

    }

}
