package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

/**
 * List of promotion runs for a promotion level
 */
@Data
public class PromotionRunView implements View {

    /**
     * The promotion level we get the view for
     */
    private final PromotionLevel promotionLevel;

    /**
     * List of promotion runs
     */
    private final List<PromotionRun> promotionRuns;

}
