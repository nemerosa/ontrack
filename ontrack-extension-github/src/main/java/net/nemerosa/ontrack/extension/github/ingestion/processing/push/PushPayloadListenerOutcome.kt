package net.nemerosa.ontrack.extension.github.ingestion.processing.push

enum class PushPayloadListenerOutcome {

    /**
     * Payload was processed
     */
    PROCESSED,

    /**
     * Payload was ignored
     */
    IGNORED,

}