package net.nemerosa.ontrack.model.extension;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.model.structure.PropertyType;

import java.util.Optional;

public interface PromotionLevelPropertyType<T> extends PropertyType<T> {

    Optional<PromotionLevel> getOrCreatePromotionLevel(T value, Branch branch, String promotionLevelName);

}
