package net.nemerosa.ontrack.model.support;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Application log entry filter.
 */
@Data
@Builder
public class ApplicationLogEntryFilter {

    private final ApplicationLogEntryLevel level;
    private final LocalDateTime before;
    private final LocalDateTime after;
    private final String authentication;
    private final String text;

    public static ApplicationLogEntryFilter none() {
        return ApplicationLogEntryFilter.builder().build();
    }
}
