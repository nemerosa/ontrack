package net.nemerosa.ontrack.model.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Application log entry filter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLogEntryFilter {

    private ApplicationLogEntryLevel level;
    private LocalDateTime before;
    private LocalDateTime after;
    private String authentication;
    private String text;

    public static ApplicationLogEntryFilter none() {
        return ApplicationLogEntryFilter.builder().build();
    }
}
