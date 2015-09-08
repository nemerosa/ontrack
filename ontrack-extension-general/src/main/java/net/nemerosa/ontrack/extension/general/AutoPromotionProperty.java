package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ValidationStamp;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

@Data
public class AutoPromotionProperty {

    /**
     * List of needed validation stamps
     */
    private final List<ValidationStamp> validationStamps;

    /**
     * Regular expression to include validation stamps by name
     */
    private final String include;

    /**
     * Regular expression to exclude validation stamps by name
     */
    private final String exclude;

    public boolean contains(ValidationStamp vs) {
        return validationStamps.stream().anyMatch(v -> (v.id() == vs.id()))
                || containsByPattern(vs);
    }

    private boolean containsByPattern(ValidationStamp vs) {
        return includes(vs) && !excludes(vs);
    }

    private boolean includes(ValidationStamp vs) {
        return matches(vs, include);
    }

    private boolean excludes(ValidationStamp vs) {
        return matches(vs, exclude);
    }

    private boolean matches(ValidationStamp vs, String pattern) {
        return StringUtils.isNotBlank(pattern) && Pattern.matches(pattern, vs.getName());
    }
}
