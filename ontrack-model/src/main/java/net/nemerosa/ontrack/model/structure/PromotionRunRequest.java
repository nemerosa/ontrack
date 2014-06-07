package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class PromotionRunRequest {

    private final int promotionLevel;
    private final LocalDateTime dateTime;
    private final String description;
    private final List<PropertyCreationRequest> properties;

    @ConstructorProperties({"promotionLevel", "dateTime", "description", "properties"})
    public PromotionRunRequest(int promotionLevel, LocalDateTime dateTime, String description, List<PropertyCreationRequest> properties) {
        this.promotionLevel = promotionLevel;
        this.dateTime = dateTime;
        this.description = description;
        this.properties = properties != null ? properties : Collections.emptyList();
    }

}
