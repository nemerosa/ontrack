package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

@Data
public class Build {

    private final ID id;
    private final String name;
    private final String description;
    private final Branch branch;
    private final List<PromotionRun> promotionRuns;
    private final List<ValidationRun> validationRuns;

}
