package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel;
import net.nemerosa.ontrack.model.structure.Reordering;

import java.util.List;
import java.util.Optional;

public interface PredefinedPromotionLevelRepository {

    List<PredefinedPromotionLevel> getPredefinedPromotionLevels();

    ID newPredefinedPromotionLevel(PredefinedPromotionLevel stamp);

    PredefinedPromotionLevel getPredefinedPromotionLevel(ID id);

    Optional<PredefinedPromotionLevel> findPredefinedPromotionLevelByName(String name);

    Document getPredefinedPromotionLevelImage(ID id);

    void savePredefinedPromotionLevel(PredefinedPromotionLevel stamp);

    Ack deletePredefinedPromotionLevel(ID predefinedPromotionLevelId);

    void setPredefinedPromotionLevelImage(ID predefinedPromotionLevelId, Document document);

    void reorderPredefinedPromotionLevels(Reordering reordering);
}
