package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class PromotionView implements View {

    private final PromotionLevel promotionLevel;
    private final Build promotedBuild;
    private final PromotionRun promotionRun;

}
