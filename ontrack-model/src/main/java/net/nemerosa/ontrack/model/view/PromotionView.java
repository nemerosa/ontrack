package net.nemerosa.ontrack.model.view;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.model.structure.PromotionRun;

@Data
public class PromotionView {

    private final PromotionLevel promotionLevel;
    private final Build promotedBuild;
    private final PromotionRun promotionRun;

}
