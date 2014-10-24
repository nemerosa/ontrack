package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class BuildFilterInput {

    @NotNull(message = "The build filter name is required.")
    @Size(min = 1, message = "The build filter name is required.")
    private final String name;
    @NotNull(message = "The build filter type is required.")
    private final String type;
    @NotNull(message = "The build filter data is required.")
    private final JsonNode data;

    private final boolean shared;

}
