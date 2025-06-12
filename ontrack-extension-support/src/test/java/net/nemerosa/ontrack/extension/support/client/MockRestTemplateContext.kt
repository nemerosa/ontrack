package net.nemerosa.ontrack.extension.support.client

interface MockRestTemplateContext {

    /**
     * Posting JSON
     */
    fun onPostJson(
        uri: String,
        body: Any,
        outcome: MockRestTemplateOutcome,
    )

    /**
     * Getting some JSON
     */
    fun onGetJson(
        uri: String,
        parameters: Map<String, String>,
        outcome: MockRestTemplateOutcome,
        expectedHeaders: Map<String, String> = emptyMap(),
    )

    /**
     * Checks that all calls have been realized.
     */
    fun verify()

    fun close()
}