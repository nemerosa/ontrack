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
     * Checks that all calls have been realized.
     */
    fun verify()

    fun close()
}