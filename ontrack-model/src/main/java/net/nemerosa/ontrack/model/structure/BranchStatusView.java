package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

/**
 * Branch with a link to the last available build, and to each promotion view.
 */
@Data
public class BranchStatusView implements View {

    private final Branch branch;
    private final Build latestBuild;
    private final List<PromotionView> promotions;

    public PromotionView getLastPromotionView() {
        if (!promotions.isEmpty()) {
            PromotionView promotionView = null;
            for (int i = promotions.size() - 1; i >= 0; i--) {
                PromotionView candidate = promotions.get(i);
                if (candidate.getPromotionRun() != null) {
                    promotionView = candidate;
                    break;
                }
            }
            return promotionView;
        } else {
            return null;
        }
    }

}
