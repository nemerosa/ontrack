package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionRun implements ProjectEntity {

    private final ID id;
    @JsonView({BranchStatusView.class, PromotionView.class, PromotionRun.class})
    private final Build build;
    @JsonView({Build.class, PromotionRun.class, BranchBuildView.class, BuildDiff.class})
    private final PromotionLevel promotionLevel;
    private final Signature signature;
    private final String description;

    public static PromotionRun of(Build build, PromotionLevel promotionLevel, Signature signature, String description) {
        return new PromotionRun(
                ID.NONE,
                build,
                promotionLevel,
                signature,
                description
        );
    }

    @Override
    public ID getProjectId() {
        return getBuild().getProjectId();
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.PROMOTION_RUN;
    }

    public PromotionRun withId(ID id) {
        return new PromotionRun(
                id,
                build,
                promotionLevel,
                signature,
                description
        );
    }

}
