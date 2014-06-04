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

    /**
     * Gets the last validation run.
     */
    public ValidationRun getLastRun() {
        if (validationRun.isEmpty()) {
            return null;
        } else {
            return validationRun.get(validationRun.size() - 1);
        }
    }

    /**
     * Gets the last status of the last run, or <code>null</code> if no run has been performed.
     */
    public ValidationRunStatus getLastStatus() {
        ValidationRun run = getLastRun();
        if (run == null) {
            return null;
        } else {
            return run.getLastStatus();
        }
    }

}
