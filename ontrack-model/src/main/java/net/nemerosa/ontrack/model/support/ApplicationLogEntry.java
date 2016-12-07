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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    @JsonIgnore
    @Wither(AccessLevel.PRIVATE)
    private final Map<String, String> details;

    public static ApplicationLogEntry error(Throwable exception, NameDescription type, String information) {
        return new ApplicationLogEntry(
                ApplicationLogEntryLevel.ERROR,
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

    public List<NameDescription> getDetailList() {
        return details.entrySet().stream()
                .map(entry -> NameDescription.nd(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
