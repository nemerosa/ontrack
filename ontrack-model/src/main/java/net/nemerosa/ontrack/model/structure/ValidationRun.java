package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationRun implements ProjectEntity {

    private final ID id;
    @JsonView({ValidationRun.class})
    private final Build build;
    @JsonView({ValidationRun.class, Build.class})
    private final ValidationStamp validationStamp;

    /**
     * Must always contain at least one validation run status at creation time.
     */
    @JsonView({ValidationRun.class, BranchBuildView.class, Build.class})
    private final List<ValidationRunStatus> validationRunStatuses;

    public ValidationRun add(ValidationRunStatus status) {
        List<ValidationRunStatus> statuses = new ArrayList<>(validationRunStatuses);
        statuses.add(0, status);
        return new ValidationRun(
                id,
                build,
                validationStamp,
                Collections.unmodifiableList(statuses)
        );
    }

    @Override
    public ID getProjectId() {
        return getBuild().getProjectId();
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.VALIDATION_RUN;
    }

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

    /**
     * The last status ("last" from a business point of view) is actually the first one in the list of statuses because
     * statuses are sorted from the most recent one to the least recent one.
     */
    public ValidationRunStatus getLastStatus() {
        return validationRunStatuses.get(0);
    }
}
