package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.client.OTHttpClientBuilder
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import org.apache.http.client.methods.HttpGet
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Network oriented tests.
 */
@AcceptanceTestSuite
class ACCNetwork extends AcceptanceTestClient {

    private Ontrack ontrack

    @Before
    void init() {
        ontrack = OntrackConnection.create(baseURL)
                .disableSsl(sslDisabled)
                .authenticate('admin', adminPassword)
                .build()
    }

    @Test
    void 'ETag and cache enabled'() {
        // Creates a promotion level
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                promotionLevel 'COPPER', 'Copper promotion'
            }
        }
        // Gets the underlying HTTP client
        def ot = OTHttpClientBuilder.create(baseURL, sslDisabled).withCredentials('admin', adminPassword).build()
        def httpClient = ot.httpClient
        // GET on the promotion level
        def url = "${baseURL}/structure/entity/promotionLevel/${project}/1.0/COPPER"
        def httpGet = new HttpGet(url)
        def response = httpClient.execute(ot.httpHost, httpGet, ot.httpClientContext)
        // ETag header
        def etag = response.getFirstHeader('ETag')
        assert etag && etag.value != ''
    }

}
