package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
@JsonPropertyOrder(alphabetic = true)
public class PromotionView implements View {

    private final PromotionLevel promotionLevel;
    @JsonView({BranchStatusView.class, PromotionView.class})
    private final Build promotedBuild;
    private final PromotionRun promotionRun;

}
