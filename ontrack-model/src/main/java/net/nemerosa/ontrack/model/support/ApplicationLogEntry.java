package net.nemerosa.ontrack.model.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.structure.NameDescription;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Application log entry.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationLogEntry {

    private final ApplicationLogEntryLevel level;
    private final LocalDateTime timestamp;
    @Wither
    private final String authentication;
    private final NameDescription type;
    @Wither
    private final Throwable exception;
    private final Map<String, String> details = new LinkedHashMap<>();

    public static ApplicationLogEntry error(Throwable exception, NameDescription type) {
        return new ApplicationLogEntry(
                ApplicationLogEntryLevel.ERROR,
                Time.now(),
                null,
                type,
                null
        ).withException(exception);
    }

    public ApplicationLogEntry withDetail(String name, String value) {
        details.put(name, value);
        return this;
    }
}
