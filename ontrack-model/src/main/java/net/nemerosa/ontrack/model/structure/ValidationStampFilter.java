package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Wither;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ValidationStampFilter implements Entity {

    @Wither
    private final ID id;
    @Wither
    private final String name;
    @Wither
    private final Project project;
    @Wither
    private final Branch branch;
    @Wither
    private final List<String> vsNames;

    public ValidationStampFilterScope getScope() {
        if (branch != null) {
            return ValidationStampFilterScope.BRANCH;
        } else if (project != null) {
            return ValidationStampFilterScope.PROJECT;
        } else {
            return ValidationStampFilterScope.GLOBAL;
        }
    }

}
