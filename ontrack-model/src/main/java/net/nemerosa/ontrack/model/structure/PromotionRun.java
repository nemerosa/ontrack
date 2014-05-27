package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionRun implements Entity {

    private final ID id;
    @JsonView({PromotionRun.class})
    private final Build build;
    @JsonView({Build.class, PromotionRun.class})
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
