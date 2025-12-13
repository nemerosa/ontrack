package net.nemerosa.ontrack.model.message

interface GlobalMessageService {

    /**
     * Gets the list of global messages
     */
    val globalMessages: List<GlobalMessage>

}