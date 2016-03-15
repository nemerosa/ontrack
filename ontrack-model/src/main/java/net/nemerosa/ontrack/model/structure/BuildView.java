package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.experimental.Wither;

import java.util.Collections;
import java.util.List;

/**
 * Build, and its list of promotions runs, and its list of validation runs.
 */
@Data
public class BuildView implements View {

    public static BuildView of(Build build) {
        return new BuildView(build, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    /**
     * The build for this view
     */
    private final Build build;

    /**
     * Decorations for the build
     */
    @Wither
    private final List<Decoration<?>> decorations;

    /**
     * The list of its promotion runs
     */
    @Wither
    private final List<PromotionRun> promotionRuns;

    /**
     * The list of its validation runs, one for each validation stamp.
     */
    @Wither
    private final List<ValidationStampRunView> validationStampRunViews;

}
