package net.nemerosa.ontrack.model.view;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.model.structure.PromotionRun;

import java.util.Optional;

@Data
public class PromotionView {

    private final PromotionLevel promotionLevel;
    private final Optional<Build> promotedBuild;
    private final Optional<PromotionRun> promotionRun;

}
