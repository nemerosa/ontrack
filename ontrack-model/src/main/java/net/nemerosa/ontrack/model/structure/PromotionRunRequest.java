package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class PromotionRunRequest {

    private final Integer promotionLevelId;
    private final String promotionLevelName;
    private final LocalDateTime dateTime;
    private final String description;
    private final List<PropertyCreationRequest> properties;

    @ConstructorProperties({"promotionLevelId", "promotionLevelName", "dateTime", "description", "properties"})
    public PromotionRunRequest(Integer promotionLevelId, String promotionLevelName, LocalDateTime dateTime, String description, List<PropertyCreationRequest> properties) {
        this.promotionLevelId = promotionLevelId;
        this.promotionLevelName = promotionLevelName;
        this.dateTime = dateTime;
        this.description = description;
        this.properties = properties != null ? properties : Collections.emptyList();
    }

}
