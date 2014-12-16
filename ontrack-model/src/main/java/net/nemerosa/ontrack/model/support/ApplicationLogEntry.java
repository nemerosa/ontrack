package net.nemerosa.ontrack.model.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.common.Time;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDateTime;

/**
 * Application log entry.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationLogEntry {

    private final ApplicationLogEntryLevel level;
    private final LocalDateTime timestamp;
    private final String source;
    private final String identifier;
    private final String context;
    private final String info;
    private final String detail;

    public ApplicationLogEntry(ApplicationLogEntryLevel level, Class<?> source, String identifier, String context, String info) {
        this(level, Time.now(), source.getName(), identifier, context, info, null);
    }

    public ApplicationLogEntry withException(Throwable exception) {
        return new ApplicationLogEntry(
                level,
                timestamp,
                source,
                identifier,
                context,
                info,
                ExceptionUtils.getStackTrace(exception)
        );
    }
}
