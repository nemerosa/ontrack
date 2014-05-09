package net.nemerosa.ontrack.model;

import lombok.Data;

@Data
public class PromotionRun {

    private final String id;
    private final String name;
    private final String description;
    private final Signature signature;
    private final PromotionLevel promotionLevel;

}
