package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

/**
 * This view is used to list all validation stamps for a build
 * with the associated runs.
 */
@Data
public class ValidationStampRunView implements View {

    private final ValidationStamp validationStamp;
    private final List<ValidationRun> validationRun;

    public boolean isPassed() {
        return !validationRun.isEmpty() && validationRun.get(validationRun.size() - 1).isPassed();
    }

    public boolean isRun() {
        return !validationRun.isEmpty();
    }

}
