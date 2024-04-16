package net.nemerosa.ontrack.extension.support.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.web.client.RestTemplate

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.support.client",
    name = ["resttemplate"],
    havingValue = "mock",
    matchIfMissing = false,
)
class MockRestTemplateProvider : DefaultRestTemplateProvider() {

    private var context: MockRestTemplateProviderContext? = null

    override fun createRestTemplate(
        rootUri: String,
        basicAuthentication: RestTemplateBasicAuthentication
    ): RestTemplate {
        val template = super.createRestTemplate(rootUri, basicAuthentication)
        context?.start(template)
        return template
    }

    fun createSession(): MockRestTemplateContext {
        val context = MockRestTemplateProviderContext()
        this.context = context
        return context
    }

    private inner class MockRestTemplateProviderContext : MockRestTemplateContext {

        private val actions = mutableListOf<MockRestTemplateAction>()
        private var mockServer: MockRestServiceServer? = null

        override fun onPostJson(path: String, body: Any, outcome: MockRestTemplateOutcome) {
            actions += MockRestTemplatePostJsonAction(
                path = path,
                body = body,
                outcome = outcome
            )
        }

        fun start(template: RestTemplate) {
            mockServer = MockRestServiceServer.createServer(template)
            mockServer?.let {
                actions.forEach { action ->
                    action.register(it)
                }
            }
        }

        override fun verify() {
            mockServer?.verify()
        }

        override fun close() {
            mockServer?.reset()
        }

    }

    private interface MockRestTemplateAction {
        fun register(mockServer: MockRestServiceServer)
    }

    private class MockRestTemplatePostJsonAction(
        private val path: String,
        private val body: Any,
        private val outcome: MockRestTemplateOutcome
    ) : MockRestTemplateAction {

        override fun register(mockServer: MockRestServiceServer) {
            mockServer
                .expect(ExpectedCount.once(), requestTo(path))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(JsonBodyMatcher(body)))
                .andRespond(outcome.responseCreator)
        }

    }

}