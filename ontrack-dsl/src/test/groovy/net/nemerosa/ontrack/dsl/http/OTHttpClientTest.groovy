package net.nemerosa.ontrack.dsl.http

import org.apache.http.HttpHost
import org.apache.http.conn.HttpHostConnectException
import org.codehaus.groovy.runtime.InvokerInvocationException
import org.junit.Test

class OTHttpClientTest {

    @Test
    void 'Retrying on connection exception'() {
        assert OTHttpClient.createDefaultRetryPolicy().canRetryFor(null, new ConnectException())
    }

    @Test
    void 'Retrying on apache connection exception'() {
        assert OTHttpClient.createDefaultRetryPolicy().canRetryFor(null, new HttpHostConnectException(new HttpHost("host"), new ConnectException()))
    }

    @Test
    void 'Retrying on apache connection exception embedded in Groovy'() {
        assert OTHttpClient.createDefaultRetryPolicy().canRetryFor(null,
                new InvokerInvocationException(
                        new HttpHostConnectException(new HttpHost("host"), new ConnectException())
                )
        )
    }

    @Test
    void 'Retrying on connection exception embedded in Groovy'() {
        assert OTHttpClient.createDefaultRetryPolicy().canRetryFor(null,
                new InvokerInvocationException(
                        new ConnectException()
                )
        )
    }

}
