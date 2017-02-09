package net.nemerosa.ontrack.model.support;

/**
 * Level of log for an application message.
 */
public enum ApplicationLogEntryLevel {

    /**
     * There were some errors during the execution of the application, and those errors
     * should be investigated.
     */
    FATAL,

    /**
     * There were some errors during the execution of the application, but those errors
     * do not prevent the rest of the application to work correctly.
     */
    ERROR

}
