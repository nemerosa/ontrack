package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel;
import net.nemerosa.ontrack.model.structure.Reordering;

import java.util.List;
import java.util.Optional;

/**
 * Management of predefined promotion levels.
 */
public interface PredefinedPromotionLevelService {

    /**
     * Gets the list of predefined promotion levels.
     */
    List<PredefinedPromotionLevel> getPredefinedPromotionLevels();

    /**
     * Creates a new predefined promotion level.
     */
    PredefinedPromotionLevel newPredefinedPromotionLevel(PredefinedPromotionLevel stamp);

    /**
     * Gets a predefined promotion level using its ID
     */
    PredefinedPromotionLevel getPredefinedPromotionLevel(ID id);

    /**
     * Gets the image for a predefined promotion level
     */
    Document getPredefinedPromotionLevelImage(ID id);

    /**
     * Gets a predefined promotion level using its name
     */
    Optional<PredefinedPromotionLevel> findPredefinedPromotionLevelByName(String name);

    /**
     * Updates the predefined promotion level
     */
    void savePredefinedPromotionLevel(PredefinedPromotionLevel stamp);

    /**
     * Deletes a predefined promotion level
     */
    Ack deletePredefinedPromotionLevel(ID predefinedPromotionLevelId);

    /**
     * Sets the image for a predefined promotion level
     */
    void setPredefinedPromotionLevelImage(ID predefinedPromotionLevelId, Document document);

    /**
     * Reorder the predefined validation stamps
     */
    void reorderPromotionLevels(Reordering reordering);
}
