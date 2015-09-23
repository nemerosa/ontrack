package net.nemerosa.ontrack.extension.stale;

import lombok.Data;
import lombok.experimental.Wither;

@Data
public class StaleProperty {

    @Wither
    private final int disablingDuration;
    @Wither
    private final int deletingDuration;

    public static StaleProperty create() {
        return new StaleProperty(0, 0);
    }
}
