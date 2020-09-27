package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.support.Selectable;

import java.util.Objects;

@Data
public class PromotionLevelSelection implements Selectable {

    private final PromotionLevel promotionLevel;
    private final boolean selected;

    @Override
    public String getId() {
        return Objects.toString(promotionLevel.id());
    }

    @Override
    public String getName() {
        return promotionLevel.getName();
    }
}
