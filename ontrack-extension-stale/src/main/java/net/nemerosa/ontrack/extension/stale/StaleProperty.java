package net.nemerosa.ontrack.extension.stale;

import lombok.Data;
import lombok.experimental.Wither;

import java.util.Collections;
import java.util.List;

@Data
public class StaleProperty {

    @Wither
    private final int disablingDuration;
    @Wither
    private final int deletingDuration;
    @Wither
    private final List<String> promotionsToKeep;

    public static StaleProperty create() {
        return new StaleProperty(0, 0, Collections.emptyList());
    }
}
