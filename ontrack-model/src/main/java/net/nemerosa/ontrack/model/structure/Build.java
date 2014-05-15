package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class Build {

    private final ID id;
    private final String name;
    private final String description;
    @JsonView({Build.class})
    private final Branch branch;
    @JsonView({Build.class})
    private final List<PromotionRun> promotionRuns;
    @JsonView({Build.class})
    private final List<ValidationRun> validationRuns;

    public static Build of(ID id, String name, String description, Branch branch) {
        return new Build(
                id,
                name,
                description,
                branch,
                Collections.<PromotionRun>emptyList(),
                Collections.<ValidationRun>emptyList()
        );
    }
}
