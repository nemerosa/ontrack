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
import java.util.LinkedHashMap;
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
    @Wither
    @JsonIgnore
    private final Throwable exception;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @JsonIgnore
    private final Map<String, String> details = new LinkedHashMap<>();

    public static ApplicationLogEntry error(Throwable exception, NameDescription type, String information) {
        return new ApplicationLogEntry(
                ApplicationLogEntryLevel.ERROR,
                Time.now(),
                null,
                type,
                information,
                null
        ).withException(exception);
    }

    public ApplicationLogEntry withDetail(String name, String value) {
        details.put(name, value);
        return this;
    }

    public List<NameDescription> getDetailList() {
        return details.entrySet().stream()
                .map(entry -> NameDescription.nd(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public String getStacktrace() {
        return exception != null ?
                ExceptionUtils.getStackTrace(exception) : "";
    }
}
