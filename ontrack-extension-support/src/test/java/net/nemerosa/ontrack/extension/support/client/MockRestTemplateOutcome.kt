package net.nemerosa.ontrack.extension.support.client

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.springframework.http.MediaType
import org.springframework.test.web.client.ResponseCreator
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

sealed interface MockRestTemplateOutcome {
    val responseCreator: ResponseCreator
}

class MockRestTemplateSuccessOutcome(
    private val body: Any,
) : MockRestTemplateOutcome {
    override val responseCreator: ResponseCreator
        get() = withSuccess(
            body.asJson().format(),
            MediaType.APPLICATION_JSON
        )
}

fun success(body: Any) = MockRestTemplateSuccessOutcome(body)
