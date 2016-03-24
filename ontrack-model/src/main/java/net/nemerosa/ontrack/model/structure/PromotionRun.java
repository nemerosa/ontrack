package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildDiff;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder(alphabetic = true)
public class PromotionRun implements ProjectEntity {

    private final ID id;
    @JsonView({ProjectStatusView.class, BranchStatusView.class, PromotionView.class, PromotionRun.class, Build.class, PromotionRunView.class})
    private final Build build;
    @JsonView({Build.class, PromotionRun.class, BranchBuildView.class, BuildDiff.class, BuildView.class, Decoration.class})
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
    public Project getProject() {
        return getBuild().getProject();
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.PROMOTION_RUN;
    }

    @Override
    public String getEntityDisplayName() {
        return String.format("Promotion run %s/%s/%s/%s",
                build.getBranch().getProject().getName(),
                build.getBranch().getName(),
                build.getName(),
                promotionLevel.getName()
        );
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
