package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class Build {

    private final ID id;
    private final String name;
    private final String description;
    private final Branch branch;
    private final List<PromotionRun> promotionRuns;
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
