package net.nemerosa.ontrack.extension.issues.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Representation of the ID of an {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration}.
 */
@Data
public class IssueServiceConfigurationIdentifier {

    private final String serviceId;
    private final String name;

    public String format() {
        return String.format("%s//%s", serviceId, name);
    }

    public static IssueServiceConfigurationIdentifier parse(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            String serviceId = StringUtils.substringBefore(value, "//").trim();
            String name = StringUtils.substringAfter(value, "//").trim();
            if (StringUtils.isNotBlank(serviceId) && StringUtils.isNotBlank(name)) {
                return new IssueServiceConfigurationIdentifier(serviceId, name);
            } else {
                throw new IssueServiceConfigurationIdentifierFormatException(value);
            }
        }
    }

}
