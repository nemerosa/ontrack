package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * API acceptance tests.
 */
@AcceptanceTestSuite
class ACCAPI extends AcceptanceTestClient {

    @Test
    void 'Access to the validation stamp API'() {
        // Prerequisites
        String project = uid('P')
        ontrack.project(project) {
            branch('master') {
                validationStamp('VS', 'Validation stamp')
            }
        }
        def vs = ontrack.validationStamp(project, 'master', 'VS')
        // Gets its API
        def api = ontrack.get("api/describe?path=/structure/validationStamps/${vs.id}")
        def getMethod = api.methods.find { it.path == 'structure/validationStamps/{validationStampId}' && it.methods == ['GET'] }
        assert getMethod != null
        assert getMethod.name == 'Get validation stamp'
    }

}
