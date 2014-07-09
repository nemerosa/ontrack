package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class BuildFilterResource {

    private final String name;
    private final BuildFilterForm form;
    private final JsonNode filter;

}
