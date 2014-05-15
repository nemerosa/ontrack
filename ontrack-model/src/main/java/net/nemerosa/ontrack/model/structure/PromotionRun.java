package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public class PromotionRun {

    private final String description;
    private final Signature signature;
    @JsonView({Build.class})
    private final PromotionLevel promotionLevel;

}
