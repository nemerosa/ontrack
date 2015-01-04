package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * Checks if the validation run view has the given validation stamp with the given status.
     */
    public boolean hasValidationStamp(String name, String status) {
        return (StringUtils.equals(name, getValidationStamp().getName()))
                && isRun()
                && (
                StringUtils.isBlank(status)
                        || StringUtils.equals(status, getLastStatus().getStatusID().getId())
        );
    }

}
