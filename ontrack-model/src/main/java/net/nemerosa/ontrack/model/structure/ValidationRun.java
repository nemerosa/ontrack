package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationRun implements Entity {

    private final ID id;
    @JsonView({ValidationRun.class})
    private final Build build;
    @JsonView({ValidationRun.class, BranchBuildView.class, Build.class})
    private final ValidationStamp validationStamp;

    /**
     * Must always contain at least one validation run status at creation time.
     */
    @JsonView({ValidationRun.class, BranchBuildView.class, Build.class})
    private final List<ValidationRunStatus> validationRunStatuses;

    public static ValidationRun of(
            Build build,
            ValidationStamp validationStamp,
            Signature signature,
            ValidationRunStatusID validationRunStatusID,
            String description) {
        List<ValidationRunStatus> statuses = Arrays.asList(
                ValidationRunStatus.of(
                        signature,
                        validationRunStatusID,
                        description
                )
        );
        return of(build, validationStamp, statuses);
    }

    public static ValidationRun of(Build build, ValidationStamp validationStamp, List<ValidationRunStatus> statuses) {
        return new ValidationRun(
                ID.NONE,
                build,
                validationStamp,
                statuses
        );
    }

    public ValidationRun withId(ID id) {
        return new ValidationRun(
                id,
                build,
                validationStamp,
                validationRunStatuses
        );
    }

    public boolean isPassed() {
        return getLastStatus().isPassed();
    }

    public ValidationRunStatus getLastStatus() {
        return validationRunStatuses.get(validationRunStatuses.size() - 1);
    }
}
