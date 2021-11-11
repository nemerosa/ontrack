package net.nemerosa.ontrack.extension.github.ingestion.processing.push

enum class PushPayloadListenerCheck {

    /**
     * Payload is to be processed
     */
    TO_BE_PROCESSED,

    /**
     * Payload is to be ignored
     */
    IGNORED,

}