package net.nemerosa.ontrack.model;

import lombok.Data;

import java.util.List;

@Data
public class Build {

    private final String id;
    private final String name;
    private final String description;
    private final Branch branch;
    private final List<PromotionRun> promotionRuns;

}
