package net.nemerosa.ontrack.model.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import java.time.LocalDateTime;

/**
 * Application log entry filter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLogEntryFilter {

    @Wither
    private ApplicationLogEntryLevel level;
    @Wither
    private LocalDateTime before;
    @Wither
    private LocalDateTime after;
    @Wither
    private String authentication;
    @Wither
    private String text;

    public static ApplicationLogEntryFilter none() {
        return ApplicationLogEntryFilter.builder().build();
    }
}
