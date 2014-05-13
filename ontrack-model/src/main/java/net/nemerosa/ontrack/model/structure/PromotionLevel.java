package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public class PromotionLevel {

    private final ID id;
    private final String name;
    private final String description;
    @JsonView({PromotionView.class})
    private final Branch branch;

}
