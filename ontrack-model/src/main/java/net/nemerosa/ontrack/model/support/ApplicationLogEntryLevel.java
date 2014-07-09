package net.nemerosa.ontrack.model.support;

/**
 * Level of log for an application message.
 */
public enum ApplicationLogEntryLevel {

    /**
     * The application has a serious issue that prevents it from working.
     */
    // FATAL,

    /**
     * There were some errors during the execution of the application, but those errors
     * do not prevent the rest of the application to work correctly
     */
    ERROR

}
