package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.structure.NameDescription;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application log entry.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ApplicationLogEntry {

    private final ApplicationLogEntryLevel level;
    private final LocalDateTime timestamp;
    @Wither
    private final String authentication;
    private final NameDescription type;
    private final String information;
    @Wither(AccessLevel.PRIVATE)
    private final String stacktrace;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Wither(AccessLevel.PRIVATE)
    private final Map<String, String> details;

    public static ApplicationLogEntry fatal(Throwable exception, NameDescription type, String information) {
        return create(ApplicationLogEntryLevel.FATAL, exception, type, information);
    }

    public static ApplicationLogEntry error(Throwable exception, NameDescription type, String information) {
        return create(ApplicationLogEntryLevel.ERROR, exception, type, information);
    }

    private static ApplicationLogEntry create(ApplicationLogEntryLevel level, Throwable exception, NameDescription type, String information) {
        return new ApplicationLogEntry(
                level,
                Time.now(),
                null,
                type,
                information,
                null,
                Collections.emptyMap()
        ).withStacktrace(ExceptionUtils.getStackTrace(exception));
    }

    public ApplicationLogEntry withDetail(String name, String value) {
        Map<String, String> map = new HashMap<>(details);
        map.put(name, value);
        return withDetails(map);
    }

    @JsonIgnore
    public Map<String, String> getDetails() {
        return details;
    }

    public List<NameDescription> getDetailList() {
        return details.entrySet().stream()
                .map(entry -> NameDescription.nd(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
