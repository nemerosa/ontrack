package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

/**
 * Acceptance tests for the access to the application status
 */
@AcceptanceTestSuite
class ACCDSLAdminStatus extends AbstractACCDSL {

    @Test
    void 'Admin status'() {
        // Status
        def status = ontrack.admin.status
        assert status.health: "Health section"
        assert status.connectors: "Connectors section"
    }

}
