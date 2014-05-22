package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.List;

@Data
public class ValidationRun {

    private final String description;
    private final Signature signature;
    @JsonView({Build.class, BranchBuildView.class})
    private final ValidationStamp validationStamp;
    @JsonView({Build.class, BranchBuildView.class})
    private final List<ValidationRunStatus> validationRunStatuses;

}
