package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class BuildFilterInput {

    private final String name;
    private final String type;
    private final JsonNode data;

}
