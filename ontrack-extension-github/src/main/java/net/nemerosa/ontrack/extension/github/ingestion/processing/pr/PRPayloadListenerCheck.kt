package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

enum class PRPayloadListenerCheck {

    /**
     * Payload is to be processed
     */
    TO_BE_PROCESSED,

    /**
     * Payload is to be ignored
     */
    IGNORED,

}