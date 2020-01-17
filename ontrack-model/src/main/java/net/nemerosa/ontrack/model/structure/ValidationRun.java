package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationRun implements RunnableEntity {

    private final ID id;
    @JsonView({ValidationRun.class, ValidationStampRunView.class})
    private final Build build;
    @JsonView({ValidationRun.class, Build.class})
    private final ValidationStamp validationStamp;
    /**
     * The run order is the order of run for the build. It starts with 1 for the first run.
     */
    private final int runOrder;

    @NotNull
    @Override
    @JsonIgnore
    public RunnableEntityType getRunnableEntityType() {
        return RunnableEntityType.validation_run;
    }

    @NotNull
    @Override
    @JsonIgnore
    public String getRunMetricName() {
        return build.getName();
    }

    @NotNull
    @Override
    @JsonIgnore
    public Map<String, String> getRunMetricTags() {
        return ImmutableMap.of(
                "project", validationStamp.getBranch().getProject().getName(),
                "branch", validationStamp.getBranch().getName(),
                "validationStamp", validationStamp.getName(),
                "status", getLastStatusId()
        );
    }

    @NotNull
    @Override
    @JsonIgnore
    public LocalDateTime getRunTime() {
        return getSignature().getTime();
    }

    /**
     * Gets the name of the last status
     */
    private String getLastStatusId() {
        if (validationRunStatuses.isEmpty()) {
            return "";
        } else {
            return validationRunStatuses.get(0).getStatusID().getId();
        }
    }

    /**
     * Data used for the link to an optional {@link ValidationDataType} and its data
     */
    @Wither
    private final ValidationRunData<?> data;

    /**
     * Must always contain at least one validation run status at creation time.
     */
    @JsonView({ValidationRun.class, BranchBuildView.class, Build.class, ValidationStampRunView.class})
    private final List<ValidationRunStatus> validationRunStatuses;

    public ValidationRun add(ValidationRunStatus status) {
        List<ValidationRunStatus> statuses = new ArrayList<>(validationRunStatuses);
        statuses.add(0, status);
        return new ValidationRun(
                id,
                build,
                validationStamp,
                runOrder,
                data,
                Collections.unmodifiableList(statuses)
        );
    }

    @Override
    public Project getProject() {
        return getBuild().getProject();
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.VALIDATION_RUN;
    }

    @Override
    public String getEntityDisplayName() {
        return String.format("Validation run %s#%d for %s/%s/%s",
                validationStamp.getName(),
                runOrder,
                build.getBranch().getProject().getName(),
                build.getBranch().getName(),
                build.getName()
        );
    }

    public static ValidationRun of(
            Build build,
            ValidationStamp validationStamp,
            int runOrder,
            Signature signature,
            ValidationRunStatusID validationRunStatusID,
            String description) {
        List<ValidationRunStatus> statuses = Collections.singletonList(
                new ValidationRunStatus(
                        ID.NONE,
                        signature,
                        validationRunStatusID,
                        description
                )
        );
        return of(build, validationStamp, runOrder, statuses);
    }

    public static ValidationRun of(Build build, ValidationStamp validationStamp, int runOrder, List<ValidationRunStatus> statuses) {
        return new ValidationRun(
                ID.NONE,
                build,
                validationStamp,
                runOrder,
                null,
                statuses
        );
    }

    public ValidationRun withId(ID id) {
        return new ValidationRun(
                id,
                build,
                validationStamp,
                runOrder,
                data,
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

    /**
     * Gets the signature of the last status
     */
    @Override
    public Signature getSignature() {
        return getLastStatus().getSignature();
    }
}
