package net.nemerosa.ontrack.model.view;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;

import java.util.List;

/**
 * Branch with a link to the last available build, and to each promotion view.
 */
@Data
public class BranchStatusView {

    private final Branch branch;
    private final Build latestBuild;
    private final List<PromotionView> promotions;

}
