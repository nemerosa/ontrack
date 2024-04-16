package net.nemerosa.ontrack.extension.support.client

sealed interface MockRestTemplateOutcome {
}

class MockRestTemplateSuccessOutcome(
    body: Any,
) : MockRestTemplateOutcome {

}

fun success(body: Any) = MockRestTemplateSuccessOutcome(body)
