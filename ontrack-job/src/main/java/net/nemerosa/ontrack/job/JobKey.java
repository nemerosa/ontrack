package net.nemerosa.ontrack.job;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class JobKey {

    private final String type;
    private final String id;

    public boolean sameType(String type) {
        return StringUtils.equals(this.type, type);
    }
}
