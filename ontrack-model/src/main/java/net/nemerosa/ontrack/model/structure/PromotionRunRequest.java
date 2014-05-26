package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionRunRequest {

    private final int promotionLevel;
    private final LocalDateTime dateTime;
    private final String description;

}
