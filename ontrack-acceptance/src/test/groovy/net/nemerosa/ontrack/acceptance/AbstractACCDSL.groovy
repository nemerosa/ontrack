package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import net.nemerosa.ontrack.dsl.http.OTMessageClientException
import org.junit.Before

/**
 * Ontrack DSL tests.
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
abstract class AbstractACCDSL extends AcceptanceTestClient {

    protected Ontrack ontrack

    @Before
    void init() {
        ontrack = ontrackAsAdmin
    }

    protected Ontrack getOntrackAsAdmin() {
        return getOntrackAs('admin', adminPassword)
    }

    protected Ontrack getOntrackAs(String user, String password) {
        return ontrackBuilder
                .authenticate(user, password)
                .build()
    }

    protected OntrackConnection getOntrackBuilder() {
        return OntrackConnection.create(baseURL).disableSsl(sslDisabled)
    }

    protected static File getImageFile() {
        def file = File.createTempFile('image', '.png')
        file.bytes = AbstractACCDSL.class.getResource('/gold.png').bytes
        file
    }

    protected static def validationError(String expectedMessage, Closure code) {
        try {
            code()
            assert false: "Should have failed with: ${expectedMessage}"
        } catch (OTMessageClientException ex) {
            assert ex.message == expectedMessage
        }
    }

}
